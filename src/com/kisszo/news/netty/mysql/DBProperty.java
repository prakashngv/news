package com.kisszo.news.netty.mysql;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value=java.lang.annotation.ElementType.FIELD)
public @interface DBProperty {
	AttributeType type();
}
