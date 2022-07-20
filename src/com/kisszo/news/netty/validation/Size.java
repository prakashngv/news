package com.kisszo.news.netty.validation;

@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME) 
public @interface Size {
	int min() default 1;
	int max() default 1;
	
	
}
