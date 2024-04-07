package io.github.kristiyan2022.envlint;

import java.util.Properties;
import java.util.Set;

public interface PropertyScanner {
    /**
     * Checks if all properties are present in a package
     * @param packageToScan the package to scan
     * @return true if all properties are present, false otherwise
     */
    boolean allPropertiesPresent(Package packageToScan);

    /**
     * Returns a set containing the missing properties keys
     * @return the missing properties
     */
    Set<String> getMissingProperties(Package packageToScan);

    /**
     * Gets the properties
     * @return the properties
     */
    Properties getProperties();
}
