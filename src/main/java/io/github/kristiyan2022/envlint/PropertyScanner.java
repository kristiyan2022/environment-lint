package io.github.kristiyan2022.envlint;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public interface PropertyScanner {
    /**
     * Checks if all properties are present in a package
     * @param packageToScan the package to scan
     * @param annotation the annotation class used to mark properties fields. The annotation must have a {@code String}
     *                   value field. The value field is used to extract the property name. If the annotation uses
     *                   another value field, or a more specific syntax for defining property keys, please use
     *                   {@link #allPropertiesPresent(Package, Class, Function)} method and provide a custom extractor
     * @return true if all properties are present, false otherwise
     */
    <T extends Annotation> boolean allPropertiesPresent(Package packageToScan, Class<T> annotation);

    /**
     * Checks if all properties are present in a package
     * @param packageToScan the package to scan
     * @param annotation the annotation class used to mark properties fields
     * @param valueExtractor A function that extracts a String value from the annotation.
     * @return true if all properties are present, false otherwise
     */
    <T extends Annotation> boolean allPropertiesPresent(Package packageToScan,
                                                        Class<T> annotation,
                                                        Function<T, String> valueExtractor);

    /**
     * Checks which properties are missing in a package
     * @param packageToScan the package to scan
     * @param annotation the annotation class used to mark properties fields. The annotation must have a {@code String}
     *                   value field. The value field is used to extract the property name. If the annotation uses
     *                   another value field, or a more specific syntax for defining property keys, please use
     *                   {@link #allPropertiesPresent(Package, Class, Function)} method and provide a custom extractor
     * @return the missing properties
     */
    <T extends Annotation> Set<String> getMissingProperties(Package packageToScan, Class<T> annotation);

    /**
     * Checks which properties are missing in a package
     * @param packageToScan the package to scan
     * @param annotation the annotation class used to mark properties fields
     * @param valueExtractor A function that extracts a String value from the annotation.
     * @return the missing properties
     */
    <T extends Annotation> Set<String> getMissingProperties(Package packageToScan,
                                                            Class<T> annotation,
                                                            Function<T, String> valueExtractor);

    /**
     * Gets the properties
     * @return the properties
     */
    Properties getProperties();
}
