package com.vermeeria.model;

/**
 * Defines the different parameter types that an action can require.
 * This information is used to determine which kind of input
 * should be shown in the user interface.
 *
 * @author Jette
 */
public enum ActionParameterKind {
    NONE,
    BOOLEAN,
    SELECTION,
    PERCENTAGE,
    TEMPERATURE
}
