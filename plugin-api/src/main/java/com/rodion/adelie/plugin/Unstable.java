package com.rodion.adelie.plugin;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;

/**
 * This annotation is an indicator that the interface or method may evolve in a way that it not
 * backwards compatible. Such as deleting methods, changing signatures, and adding checked
 * exceptions. Authors are advised to exercise caution when using these APIs.
 */
@Retention(CLASS)
@java.lang.annotation.Target({METHOD, TYPE})
public @interface Unstable {}
