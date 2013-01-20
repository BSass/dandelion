package com.github.dandelion.core.utils.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dandelion.core.utils.ClassPathResource;
import com.github.dandelion.core.utils.ClassUtils;
import com.github.dandelion.core.utils.StringUtils;
import com.github.dandelion.core.utils.UrlUtils;

/**
 * ClassPath scanner.
 */
public class ClassPathScanner {

    // Logger
 	private static final Logger LOG = LoggerFactory.getLogger(ClassPathScanner.class);
 	
    /**
     * Scans the classpath for resources under the specified location, starting with the specified prefix and ending with
     * the specified suffix.
     * 
     * @param location The location (directory) in the classpath to start searching. Subdirectories are also searched.
     * @param prefix   The prefix of the resource names to match.
     * @param suffix   The suffix of the resource names to match.
     * @return The resources that were found.
     * @throws IOException when the location could not be scanned.
     */
    public ClassPathResource[] scanForResources(String location, String prefix, String suffix) throws IOException {
        LOG.debug("Scanning for resources at '" + location + "' (Prefix: '" + prefix + "', Suffix: '" + suffix + "')");

        Set<ClassPathResource> classPathResources = new TreeSet<ClassPathResource>();

        Set<String> resourceNames = findResourceNames(location, prefix, suffix);
        for (String resourceName : resourceNames) {
            classPathResources.add(new ClassPathResource(resourceName));
            LOG.debug("Found resource: " + resourceName);
        }

        return classPathResources.toArray(new ClassPathResource[classPathResources.size()]);
    }

    /**
     * Scans the classpath for concrete classes under the specified package implementing any of these interfaces.
     * Non-instantiable abstract classes are filtered out.
     *
     * @param location              The location (package) in the classpath to start scanning.
     *                              Subpackages are also scanned.
     * @param implementedInterfaces The interfaces the matching classes should implement..
     * @return The non-abstract classes that were found.
     * @throws Exception when the location could not be scanned.
     */
    public Class<?>[] scanForClasses(String location, Class<?>... implementedInterfaces) throws Exception {
        String[] interfaceNames = new String[implementedInterfaces.length];
        for (int i = 0; i < implementedInterfaces.length; i++) {
            interfaceNames[i] = implementedInterfaces[i].getName();
        }
        LOG.debug("Scanning for classes at '" + location
                + "' (Implementing: '" + StringUtils.arrayToCommaDelimitedString(interfaceNames) + "')");

        List<Class<?>> classes = new ArrayList<Class<?>>();

        Set<String> resourceNames = findResourceNames(location, "", ".class");
        for (String resourceName : resourceNames) {
            String className = toClassName(resourceName);
            Class<?> clazz = getClassLoader().loadClass(className);

            if (!ClassUtils.canInstantiate(clazz)) {
                LOG.debug("Skipping uninstantiable class: " + className);
                continue;
            }

            if (implementedInterfaces.length == 0) {
                classes.add(clazz);
                LOG.debug("Found class: " + className);
            } else {
                for (Class<?> implementedInterface : implementedInterfaces) {
                    if (implementedInterface.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                        LOG.debug("Found class: " + className);
                        break;
                    }
                }
            }
        }

        return classes.toArray(new Class<?>[classes.size()]);
    }

    /**
     * Converts this resource name to a fully qualified class name.
     *
     * @param resourceName The resource name.
     * @return The class name.
     */
    private String toClassName(String resourceName) {
        String nameWithDots = resourceName.replace("/", ".");
        return nameWithDots.substring(0, (nameWithDots.length() - ".class".length()));
    }

    /**
     * Finds the resources names present at this location and below on the classpath starting with this prefix and
     * ending with this suffix.
     *
     * @param location The location on the classpath to scan.
     * @param prefix   The filename prefix to match.
     * @param suffix   The filename suffix to match.
     * @return The resource names.
     * @throws IOException when scanning this location failed.
     */
    private Set<String> findResourceNames(String location, String prefix, String suffix) throws IOException {
        Set<String> resourceNames = new TreeSet<String>();

        Enumeration<URL> locationsUrls = getClassLoader().getResources(location);
        if (!locationsUrls.hasMoreElements()) {
            LOG.debug("Unable to determine URL for classpath location: " + location + " (ClassLoader: " + getClassLoader() + ")");
        }
        while (locationsUrls.hasMoreElements()) {
            URL locationUrl = locationsUrls.nextElement();
            LOG.debug("Scanning URL: " + locationUrl.toExternalForm());

            UrlResolver urlResolver = createUrlResolver(locationUrl.getProtocol());
            URL resolvedUrl = urlResolver.toStandardJavaUrl(locationUrl);

            String scanRoot = UrlUtils.toFilePath(resolvedUrl);

            String protocol = resolvedUrl.getProtocol();
            LocationScanner locationScanner = createLocationScanner(protocol);
            if (locationScanner == null) {
                LOG.warn("Unable to scan location: " + scanRoot + " (unsupported protocol: " + protocol + ")");
            } else {
                resourceNames.addAll(locationScanner.findResourceNames(location, resolvedUrl));
            }
        }

        return filterResourceNames(resourceNames, prefix, suffix);
    }

    /**
     * Creates an appropriate URL resolver scanner for this url protocol.
     *
     * @param protocol The protocol of the location url to scan.
     * @return The url resolver for this protocol.
     */
    private UrlResolver createUrlResolver(String protocol) {
        return new UrlResolver() {
            public URL toStandardJavaUrl(URL url) throws IOException {
                return url;
            }
        };
    }

    /**
     * Creates an appropriate location scanner for this url protocol.
     *
     * @param protocol The protocol of the location url to scan.
     * @return The location scanner or {@code null} if it could not be created.
     */
    private LocationScanner createLocationScanner(String protocol) {
        if ("file".equals(protocol)) {
            return new FileSystemLocationScanner();
        }

        if ("jar".equals(protocol)
                || "zip".equals(protocol) //WebLogic
                || "wsjar".equals(protocol) //WebSphere
                ) {
            return new JarFileLocationScanner();
        }

        return null;
    }

    /**
     * @return The classloader to use to scan the classpath.
     */
    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Filters this list of resource names to only include the ones whose filename matches this prefix and this suffix.
     *
     * @param resourceNames The names to filter.
     * @param prefix        The prefix to match.
     * @param suffix        The suffix to match.
     * @return The filtered names set.
     */
    private Set<String> filterResourceNames(Set<String> resourceNames, String prefix, String suffix) {
        Set<String> filteredResourceNames = new TreeSet<String>();
        for (String resourceName : resourceNames) {
            String fileName = resourceName.substring(resourceName.lastIndexOf("/") + 1);
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix)
                    && (fileName.length() > (prefix + suffix).length())) {
                filteredResourceNames.add(resourceName);
            } else {
                LOG.debug("Filtering out resource: " + resourceName + " (filename: " + fileName + ")");
            }
        }
        return filteredResourceNames;
    }
}
