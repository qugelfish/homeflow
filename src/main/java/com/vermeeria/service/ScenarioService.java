package com.vermeeria.service;

import com.vermeeria.model.ExecutionLogEntry;
import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides scenario-related business logic.
 *
 * @author Jette
 */
public class ScenarioService {

    private static final String SELECTED_SCENARIO_NO_LONGER_EXISTS = "The selected scenario no longer exists.";
    private final SmartHomeService smartHomeService;

    /**
     * Creates a scenario service backed by the shared application state.
     *
     * @param smartHomeService the shared smart home service
     */
    public ScenarioService(final SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    /**
     * Returns all configured scenarios sorted by name.
     *
     * @return the configured scenarios
     */
    public List<Scenario> getAllScenarios() {
        return smartHomeService.getAppData().getScenarios().stream().sorted((firstScenario, secondScenario) -> firstScenario.getName().compareToIgnoreCase(secondScenario.getName())).toList();
    }

    /**
     * Returns all execution log entries in reverse chronological order.
     *
     * @return the execution log entries
     */
    public List<ExecutionLogEntry> getExecutionLogs() {
        return smartHomeService.getAppData().getLogs().stream()
                .sorted((firstLog, secondLog) -> {
                    if (firstLog.getExecutedAt() == null && secondLog.getExecutedAt() == null) {
                        return 0;
                    }
                    if (firstLog.getExecutedAt() == null) {
                        return 1;
                    }
                    if (secondLog.getExecutedAt() == null) {
                        return -1;
                    }
                    return secondLog.getExecutedAt().compareTo(firstLog.getExecutedAt());
                })
                .toList();
    }

    /**
     * Creates a new scenario.
     *
     * @param scenarioName the scenario name
     * @param description  the scenario description
     */
    public void createScenario(final String scenarioName, final String description) {
        createScenario(scenarioName, description, List.of());
    }

    /**
     * Creates a new scenario with the given action list.
     *
     * @param scenarioName the scenario name
     * @param description  the scenario description
     * @param actions      the configured scenario actions
     */
    public void createScenario(final String scenarioName, final String description, final List<ScenarioAction> actions) {
        String normalizedName = validateAndNormalizeScenarioName(scenarioName);
        validateUniqueScenarioName(normalizedName, null);

        Scenario scenario = new Scenario(normalizedName, normalizeDescription(description));
        scenario.setActions(copyActions(actions));
        smartHomeService.getAppData().addScenario(scenario);
        smartHomeService.saveAll();
    }

    /**
     * Updates an existing scenario.
     *
     * @param scenarioId   the scenario identifier
     * @param scenarioName the updated scenario name
     * @param description  the updated description
     */
    public void updateScenario(final String scenarioId, final String scenarioName, final String description) {
        updateScenario(scenarioId, scenarioName, description, null);
    }

    /**
     * Updates an existing scenario and optionally replaces its actions.
     *
     * @param scenarioId   the scenario identifier
     * @param scenarioName the updated scenario name
     * @param description  the updated description
     * @param actions      the updated actions or null to keep the current actions
     */
    public void updateScenario(final String scenarioId, final String scenarioName, final String description, final List<ScenarioAction> actions) {
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new ValidationException("Please select a scenario to edit.");
        }

        String normalizedName = validateAndNormalizeScenarioName(scenarioName);
        validateUniqueScenarioName(normalizedName, scenarioId);

        Scenario scenarioToUpdate = smartHomeService.getAppData().getScenarios().stream().filter(scenario -> scenarioId.equals(scenario.getId())).findFirst().orElseThrow(() -> new ValidationException(SELECTED_SCENARIO_NO_LONGER_EXISTS));

        scenarioToUpdate.setName(normalizedName);
        scenarioToUpdate.setDescription(normalizeDescription(description));
        if (actions != null) {
            scenarioToUpdate.setActions(copyActions(actions));
        }
        smartHomeService.saveAll();
    }

    /**
     * Replaces only the actions of an existing scenario.
     *
     * @param scenarioId the scenario identifier
     * @param actions    the updated actions
     */
    public void updateScenarioActions(final String scenarioId, final List<ScenarioAction> actions) {
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new ValidationException("Please select a scenario first.");
        }

        Scenario scenarioToUpdate = smartHomeService.getAppData().getScenarios().stream().filter(scenario -> scenarioId.equals(scenario.getId())).findFirst().orElseThrow(() -> new ValidationException(SELECTED_SCENARIO_NO_LONGER_EXISTS));

        scenarioToUpdate.setActions(copyActions(actions));
        smartHomeService.saveAll();
    }

    /**
     * Deletes an existing scenario.
     *
     * @param scenarioId the scenario identifier
     */
    public void deleteScenario(final String scenarioId) {
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new ValidationException("Please select a scenario to delete.");
        }

        Scenario scenarioToDelete = smartHomeService.getAppData().getScenarios().stream().filter(scenario -> scenarioId.equals(scenario.getId())).findFirst().orElseThrow(() -> new ValidationException(SELECTED_SCENARIO_NO_LONGER_EXISTS));

        smartHomeService.getAppData().removeScenario(scenarioToDelete);
        smartHomeService.saveAll();
    }

    /**
     * Executes all actions of the selected scenario and writes execution logs.
     *
     * @param scenarioId    the scenario identifier
     * @param deviceService the device service used to apply device actions
     * @return the created log entries
     */
    public List<ExecutionLogEntry> executeScenario(final String scenarioId,
                                                   final DeviceService deviceService) {
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new ValidationException("Please select a scenario to run.");
        }

        Scenario scenarioToExecute = findScenarioById(scenarioId);
        if (scenarioToExecute.getActions() == null || scenarioToExecute.getActions().isEmpty()) {
            throw new ValidationException("The selected scenario has no actions.");
        }

        List<ExecutionLogEntry> createdLogs = new ArrayList<>();
        for (ScenarioAction action : scenarioToExecute.getActions()) {
            String message;
            try {
                message = "Executed " + deviceService.executeScenarioAction(action);
            } catch (RuntimeException exception) {
                message = "Failed to execute action: " + exception.getMessage();
            }

            ExecutionLogEntry logEntry = new ExecutionLogEntry(
                    scenarioToExecute.getId(),
                    scenarioToExecute.getName(),
                    message
            );
            smartHomeService.getAppData().addLogEntry(logEntry);
            createdLogs.add(logEntry);
        }

        smartHomeService.saveAll();
        return createdLogs;
    }

    private String validateAndNormalizeScenarioName(final String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            throw new ValidationException("Scenario name cannot be empty.");
        }
        return scenarioName.trim();
    }

    private String normalizeDescription(final String description) {
        return description == null ? "" : description.trim();
    }

    private void validateUniqueScenarioName(final String scenarioName, final String currentScenarioId) {
        boolean duplicateNameExists = smartHomeService.getAppData().getScenarios().stream().filter(scenario -> currentScenarioId == null || !currentScenarioId.equals(scenario.getId())).map(Scenario::getName).anyMatch(existingName -> existingName.equalsIgnoreCase(scenarioName));

        if (duplicateNameExists) {
            throw new ValidationException("A scenario with this name already exists.");
        }
    }

    private Scenario findScenarioById(final String scenarioId) {
        return smartHomeService.getAppData().getScenarios().stream()
                .filter(scenario -> scenarioId.equals(scenario.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException(SELECTED_SCENARIO_NO_LONGER_EXISTS));
    }

    private List<ScenarioAction> copyActions(final List<ScenarioAction> actions) {
        if (actions == null) {
            return new ArrayList<>();
        }

        return actions.stream().map(action -> new ScenarioAction(action.getDeviceId(), action.getActionKey(), action.getParameterValue())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
