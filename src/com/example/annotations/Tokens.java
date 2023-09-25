package com.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS) public @interface Tokens
{
	String[] focalMethod() default {};

	String[] focalClass() default {};

	String[] state() default {};

	String[] variable() default {};

	String[] parameter() default {};

	String[] expectedResult() default {};

	String[] exception() default {};

	String[] scenario() default {};
}