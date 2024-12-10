package com.cheil.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReflectionDataService {

    Logger LOG = LoggerFactory.getLogger(ReflectionDataService.class);

    /**
     * returns all class names from the given package
     *
     * @param packageName
     * @return
     */
    public static Set<String> findAllClassesUsingReflectionsLibrary(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream().filter(testClass -> !Modifier.isAbstract(testClass.getModifiers()) && CollectionUtils.isNotEmpty(Arrays.asList(testClass.getDeclaredMethods())))
                .map(Class::getSimpleName) // Map to simple names
                .collect(Collectors.toSet());
    }

    /**
     * returns all public method names in the given class
     *
     * @param className
     * @return
     */
    public Set<String> getMethodNamesForClass(String className) {
        Set<String> methodNames = new HashSet<>();

        try {
            // Load the class by its name
            Class<?> clazz = Class.forName("com.cheil.qa.pages.concretepages." + className);

            // Get all declared methods of the class
            Method[] methods = clazz.getDeclaredMethods();

            // Iterate through the methods and add their names to the set
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    methodNames.add(method.getName());
                }
            }
        } catch (ClassNotFoundException e) {
            // Handle the exception if the class is not found
            LOG.error(e.getMessage());
        }

        return methodNames;
    }
}

