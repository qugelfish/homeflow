package com.vermeeria.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Shared reflection-based support for model tests.
 *
 * @author Jette
 */
abstract class AbstractModelTestHelper {

    protected <T> void assertBeanPropertiesRoundTrip(final Class<T> modelClass, final Map<String, Object> propertyValues) throws Exception {
        T instance = instantiate(modelClass);

        for (Map.Entry<String, Object> propertyEntry : propertyValues.entrySet()) {
            String propertyName = propertyEntry.getKey();
            Object expectedValue = propertyEntry.getValue();

            // Resolve standard bean accessors dynamically so concrete model tests only have to declare which properties should round-trip.
            Method setter = modelClass.getMethod(setterName(propertyName), expectedValue.getClass());
            Method getter = modelClass.getMethod(getterName(propertyName));

            setter.invoke(instance, expectedValue);

            Object actualValue = getter.invoke(instance);
            assertEquals(expectedValue, actualValue, () -> "Unexpected value for property '" + propertyName + "'");
        }
    }

    protected <T> void assertIdPrefix(final Class<T> modelClass, final String expectedPrefix) throws Exception {
        T instance = instantiate(modelClass);
        Method getIdMethod = modelClass.getMethod("getId");

        Object rawId = getIdMethod.invoke(instance);

        assertInstanceOf(String.class, rawId, "The id should be a String.");
        String id = (String) rawId;
        assertFalse(id.isBlank(), "The generated id should not be blank.");
        assertTrue(id.startsWith(expectedPrefix), () -> "The id should start with '" + expectedPrefix + "'.");
    }

    protected <T> void assertListSetterDefensivelyCopies(final Class<T> modelClass, final String propertyName, final List<?> initialList, final Object additionalElement) throws Exception {
        T instance = instantiate(modelClass);
        Method setter = modelClass.getMethod(setterName(propertyName), List.class);
        Method getter = modelClass.getMethod(getterName(propertyName));

        @SuppressWarnings("unchecked") List<Object> mutableInput = (List<Object>) new java.util.ArrayList<>(initialList);
        setter.invoke(instance, mutableInput);

        // Mutate the caller-owned list after invoking the setter. A correct model implementation should have copied the list, so this change stays invisible.
        mutableInput.add(additionalElement);

        @SuppressWarnings("unchecked") List<Object> actualList = (List<Object>) getter.invoke(instance);

        assertEquals(initialList, actualList, () -> "The property '" + propertyName + "' should be defensively copied.");
    }

    protected <T> void assertMapSetterDefensivelyCopies(final Class<T> modelClass, final String propertyName, final Map<?, ?> initialMap, final String additionalKey, final Object additionalValue) throws Exception {
        T instance = instantiate(modelClass);
        Method setter = modelClass.getMethod(setterName(propertyName), Map.class);
        Method getter = modelClass.getMethod(getterName(propertyName));

        @SuppressWarnings("unchecked") Map<String, Object> mutableInput = (Map<String, Object>) new java.util.LinkedHashMap<>(initialMap);
        setter.invoke(instance, mutableInput);

        // The same defensive-copy idea as above, but for mutable maps.
        mutableInput.put(additionalKey, additionalValue);

        @SuppressWarnings("unchecked") Map<String, Object> actualMap = (Map<String, Object>) getter.invoke(instance);

        assertEquals(initialMap, actualMap, () -> "The property '" + propertyName + "' should be defensively copied.");
    }

    protected <T> T instantiate(final Class<T> modelClass) throws Exception {
        // All current model classes provide a no-args constructor, which keeps the shared reflection helpers small and reusable across the whole model package.
        Constructor<T> constructor = modelClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private String setterName(final String propertyName) {
        return "set" + capitalize(propertyName);
    }

    private String getterName(final String propertyName) {
        return "get" + capitalize(propertyName);
    }

    private String capitalize(final String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
