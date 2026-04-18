package com.vermeeria.service;

import com.vermeeria.model.Scenario;

import java.util.List;

/**
 * Provides scenario-related business logic.
 *
 * @author Jette
 */
public class ScenarioService {

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
        return smartHomeService.getAppData().getScenarios().stream()
                .sorted((firstScenario, secondScenario) -> firstScenario.getName().compareToIgnoreCase(secondScenario.getName()))
                .toList();
    }

    /**
     * Creates a new scenario.
     *
     * @param scenarioName the scenario name
     * @param description  the scenario description
     */
    public void createScenario(final String scenarioName, final String description) {
        String normalizedName = validateAndNormalizeScenarioName(scenarioName);
        validateUniqueScenarioName(normalizedName, null);

        Scenario scenario = new Scenario(normalizedName, normalizeDescription(description));
        smartHomeService.getAppData().getScenarios().add(scenario);
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
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new ValidationException("Please select a scenario to edit.");
        }

        String normalizedName = validateAndNormalizeScenarioName(scenarioName);
        validateUniqueScenarioName(normalizedName, scenarioId);

        Scenario scenarioToUpdate = smartHomeService.getAppData().getScenarios().stream()
                .filter(scenario -> scenarioId.equals(scenario.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("The selected scenario no longer exists."));

        scenarioToUpdate.setName(normalizedName);
        scenarioToUpdate.setDescription(normalizeDescription(description));
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

        Scenario scenarioToDelete = smartHomeService.getAppData().getScenarios().stream()
                .filter(scenario -> scenarioId.equals(scenario.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("The selected scenario no longer exists."));

        smartHomeService.getAppData().getScenarios().remove(scenarioToDelete);
        smartHomeService.saveAll();
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
        boolean duplicateNameExists = smartHomeService.getAppData().getScenarios().stream()
                .filter(scenario -> currentScenarioId == null || !currentScenarioId.equals(scenario.getId()))
                .map(Scenario::getName)
                .anyMatch(existingName -> existingName.equalsIgnoreCase(scenarioName));

        if (duplicateNameExists) {
            throw new ValidationException("A scenario with this name already exists.");
        }
    }
}
