package com.twi.security.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 防止重复提交注解，用于方法上<br/>
 * 在新建页面方法上，设置save()为true，此时拦截器会在Session中保存一个token，
 * 同时需要在新建的页面中添加
 * <input type="hidden" name="token" value="${token}">
 * <br/>
 * 保存方法需要验证重复提交的，设置remove为true
 * 此时会在拦截器中验证是否重复提交
 * </p>
 * @date: 2013-6-27上午11:14:02
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
	/**
	 * 保存token
	 * @return
	 */
    public boolean save() default false;
    
    /**
     * 删除token
     * @return
     */
    boolean remove() default false;
}