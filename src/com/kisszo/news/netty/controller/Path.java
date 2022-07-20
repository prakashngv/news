package com.kisszo.news.netty.controller;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Path {
	String path() default "";
}
