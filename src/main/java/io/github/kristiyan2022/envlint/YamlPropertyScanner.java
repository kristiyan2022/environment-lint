package io.github.kristiyan2022.envlint;

import io.github.kristiyan2022.envlint.util.PropertyMapFlattener;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class YamlPropertyScanner implements PropertyScanner {
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
    public boolean allPropertiesPresent(Package packageToScan) {
        return this.getMissingProperties(packageToScan).isEmpty();
    }

    @Override
    public Set<String> getMissingProperties(Package packageToScan) {
        Set<String> requiredEnvironmentVariables = extractEnvVariablesFromPackage(packageToScan);

        return requiredEnvironmentVariables.stream()
                .filter(envVariable -> !this.properties.containsKey(envVariable))
                .collect(Collectors.toSet());
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    private Set<String> extractEnvVariablesFromPackage(Package pkg) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(pkg.getName())
                .setScanners(Scanners.FieldsAnnotated));

        // Find all fields across classes in the package annotated with @Value
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Value.class);

        return fields.stream()
                .map(field -> field.getAnnotation(Value.class).value())
                .filter(value -> value.matches("\\$\\{(.+?)(?::(.+))?}"))
                .map(value -> value.substring(2, value.length() - 1).split(":")[0])
                .collect(Collectors.toSet());
    }
}
