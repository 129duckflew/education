package cn.duckflew.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Target(ElementType.METHOD)  // 作用到方法上
@Retention(RetentionPolicy.RUNTIME)
public  @interface NoRepeatSubmit
{
// 运行时有效
}