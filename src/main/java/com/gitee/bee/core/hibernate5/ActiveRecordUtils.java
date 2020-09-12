package com.gitee.bee.core.hibernate5;

import org.springframework.context.ApplicationContext;

/**
 * Created by libinsong on 2020/9/11 10:31 下午
 */
abstract public class ActiveRecordUtils {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
}
