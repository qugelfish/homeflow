package com.vermeeria.model;

/**
 * Describes an action that a device type supports.
 * It contains the technical key, a user-friendly label
 * and information about the required parameter.
 *
 * @author Jette
 */
public record ActionSpec(
        String actionKey,
        String label,
        ActionParameterKind parameterKind,
        String parameterLabel
) {
}
