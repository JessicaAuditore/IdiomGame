package org.soul.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    int maxSize() default 12;

    boolean notNull() default false;
}
