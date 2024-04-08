package io.github.kristiyan2022.envlint;

import io.github.kristiyan2022.envlint.util.PropertyMapFlattener;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class YamlPropertyScanner implements PropertyScanner {
    private static final Logger log = LoggerFactory.getLogger(YamlPropertyScanner.class);
    private final Properties properties;

    public YamlPropertyScanner(Properties properties) {
        this.properties = properties;
    }

    public YamlPropertyScanner(File propertyFile) throws IOException {
        this(new Properties());

        Yaml yaml = new Yaml();
        try (FileInputStream input = new FileInputStream(propertyFile)) {
            Map<String, String> environmentVariables = new PropertyMapFlattener().map(yaml.load(input));
            this.properties.putAll(environmentVariables);
        }
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public <T extends Annotation> boolean allPropertiesPresent(Package packageToScan, Class<T> annotation) {
        return this.allPropertiesPresent(packageToScan, annotation, defaultExtractor());
    }

    @Override
    public <T extends Annotation> boolean allPropertiesPresent(Package packageToScan,
                                                               Class<T> annotation,
                                                               Function<T, String> valueExtractor) {
        return this.extractEnvVariablesFromPackage(packageToScan, annotation, valueExtractor).isEmpty();
    }

    @Override
    public <T extends Annotation> Set<String> getMissingProperties(Package packageToScan, Class<T> annotation) {
        return this.getMissingProperties(packageToScan, annotation, defaultExtractor());
    }

    @Override
    public <T extends Annotation> Set<String> getMissingProperties(Package packageToScan,
                                                                   Class<T> annotation,
                                                                   Function<T, String> valueExtractor) {
        Set<String> requiredVariables = extractEnvVariablesFromPackage(packageToScan, annotation, valueExtractor);
        System.out.println("Required variables: " + requiredVariables);
        System.out.println("Properties: " + this.properties);

        return requiredVariables.stream()
                .filter(envVariable -> !this.properties.containsKey(envVariable))
                .collect(Collectors.toSet());
    }


    private <T extends Annotation> Set<String> extractEnvVariablesFromPackage(Package pkg,
                                                                              Class<T> annotation,
                                                                              Function<T, String> valueExtractor) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(pkg.getName())
                .setScanners(Scanners.FieldsAnnotated));

        // Find all fields across classes in the package annotated with the specified annotation
        Set<Field> fields = reflections.getFieldsAnnotatedWith(annotation);

        return fields.stream()
                .map(field -> valueExtractor.apply(field.getAnnotation(annotation)))
                .collect(Collectors.toSet());
    }

    private <T extends Annotation> Function<T, String> defaultExtractor() {
        return annotation -> {
            try {
                Method valueMethod = annotation.getClass().getMethod("value");
                System.out.println("Annotation value method: " + valueMethod);
                System.out.println("Annotation value: " + valueMethod.invoke(annotation));
                return (String) valueMethod.invoke(annotation);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Annotation must have a 'value' method. If the annotation has a " +
                        "different method name, please provide a custom extractor to the method.", e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Could not access the 'value' method of the annotation.", e);
            }
        };
    }
}
