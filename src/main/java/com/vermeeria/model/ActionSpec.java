package com.vermeeria.model;

import java.util.List;

/**
 * Describes an action that a device type supports.
 * It contains the technical key, a user-friendly label
 * and information about the required parameter.
 *
 * @author Jette
 */
public record ActionSpec(String actionKey, String label, ActionParameterKind parameterKind, String parameterLabel, List<String> allowedValues) {

    /**
     * Creates an action specification without predefined selectable values.
     *
     * @param actionKey      the technical action key
     * @param label          the user-facing action label
     * @param parameterKind  the expected parameter kind
     * @param parameterLabel the user-facing parameter label
     */
    public ActionSpec(final String actionKey, final String label, final ActionParameterKind parameterKind, final String parameterLabel) {
        this(actionKey, label, parameterKind, parameterLabel, List.of());
    }

    /**
     * Creates an action specification and normalizes the selectable values list.
     */
    public ActionSpec {
        allowedValues = allowedValues == null ? List.of() : List.copyOf(allowedValues);
    }
}
