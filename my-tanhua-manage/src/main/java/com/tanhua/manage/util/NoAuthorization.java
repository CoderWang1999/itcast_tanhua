package com.tanhua.manage.util;

import java.lang.annotation.*;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented //标记注解
public @interface NoAuthorization {

}