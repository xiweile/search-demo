package com.weiller.search.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SearchField {
    String name() default "";

    FiledType type() default FiledType.STRING;

    boolean indexed() default false;

    boolean stored() default true;

}
