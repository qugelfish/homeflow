package com.vermeeria.plugin;

import com.vermeeria.service.ValidationException;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Discovers and provides all available device plugins.
 * New device types can be added by creating another
 * {@link DevicePlugin} implementation in the plugin package.
 *
 * @author Jette
 */
public class DevicePluginRegistry {

    private static final String PLUGIN_PACKAGE = "com.vermeeria.plugin";
    private static final String PLUGIN_PACKAGE_PATH = PLUGIN_PACKAGE.replace('.', '/');
    private static final String DOT_CLASS = ".class";
    private static final int MAX_JAR_ENTRIES_TO_SCAN = 1_000;
    private static final long MAX_CLASS_ENTRY_SIZE = 1_048_576L;
    private final Map<String, DevicePlugin> pluginsByTypeKey;

    /**
     * Creates the registry and scans the plugin package.
     */
    public DevicePluginRegistry() {
        this.pluginsByTypeKey = new LinkedHashMap<>();
        loadPlugins();
    }

    /**
     * Returns all discovered plugins sorted by display name.
     *
     * @return the available plugins
     */
    public List<DevicePlugin> getAllPlugins() {
        return pluginsByTypeKey.values().stream().sorted(Comparator.comparing(DevicePlugin::getDisplayName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    /**
     * Returns the plugin for the given device type.
     *
     * @param typeKey the device type key
     * @return the matching plugin
     */
    public DevicePlugin getPlugin(final String typeKey) {
        DevicePlugin plugin = pluginsByTypeKey.get(typeKey);
        if (plugin == null) {
            throw new ValidationException("Unknown device type: " + typeKey);
        }
        return plugin;
    }

    /**
     * Returns whether a plugin exists for the given type key.
     *
     * @param typeKey the device type key
     * @return true if a plugin exists
     */
    public boolean hasPlugin(final String typeKey) {
        return pluginsByTypeKey.containsKey(typeKey);
    }

    private void loadPlugins() {
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(PLUGIN_PACKAGE_PATH);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    loadPluginsFromDirectory(resource);
                } else if ("jar".equals(resource.getProtocol())) {
                    loadPluginsFromJar(resource);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to scan device plugins.", exception);
        }
    }

    private void loadPluginsFromDirectory(final URL resource) {
        String decodedPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
        File directory = new File(decodedPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(DOT_CLASS));

        if (files == null) {
            return;
        }

        for (File file : files) {
            registerPluginClassName(file.getName());
        }
    }

    private void loadPluginsFromJar(final URL resource) throws IOException {
        JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
        jarConnection.setUseCaches(false);
        try (JarFile jarFile = jarConnection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            int scannedEntries = 0;
            while (entries.hasMoreElements()) {
                if (scannedEntries++ >= MAX_JAR_ENTRIES_TO_SCAN) {
                    throw new IllegalStateException("Refusing to scan an unexpectedly large plugin archive.");
                }

                JarEntry entry = entries.nextElement();
                validateJarEntry(entry);
                String entryName = entry.getName();
                if (entryName.startsWith(PLUGIN_PACKAGE_PATH) && !entry.isDirectory() && entryName.endsWith(DOT_CLASS) && !entryName.contains("$")) {
                    String simpleName = entryName.substring(entryName.lastIndexOf('/') + 1);
                    registerPluginClassName(simpleName);
                }
            }
        }
    }

    private void validateJarEntry(final JarEntry entry) {
        long entrySize = entry.getSize();
        long compressedSize = entry.getCompressedSize();

        if (entrySize > MAX_CLASS_ENTRY_SIZE || compressedSize > MAX_CLASS_ENTRY_SIZE) {
            throw new IllegalStateException("Refusing to scan an oversized plugin archive entry: " + entry.getName());
        }
    }

    private void registerPluginClassName(final String classFileName) {
        String simpleClassName = classFileName.replace(DOT_CLASS, "");
        String fullyQualifiedName = PLUGIN_PACKAGE + "." + simpleClassName;

        try {
            Class<?> pluginClass = Class.forName(fullyQualifiedName);
            if (!DevicePlugin.class.isAssignableFrom(pluginClass) || pluginClass.isInterface() || pluginClass.equals(DevicePluginRegistry.class)) {
                return;
            }

            DevicePlugin plugin = (DevicePlugin) pluginClass.getDeclaredConstructor().newInstance();
            pluginsByTypeKey.put(plugin.getTypeKey(), plugin);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to initialize device plugin: " + fullyQualifiedName, exception);
        }
    }
}
