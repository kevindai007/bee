package com.gitee.bee.core.hibernate5;

import com.gitee.bee.model.ActiveRecord;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by libinsong on 2020/9/11 10:54 下午
 */
public class ActiveRecordInitializer implements InitializingBean, ApplicationContextAware {

    @Autowired
    private SessionFactory sessionFactory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PlatformTransactionManager transactionManager =
                applicationContext.getBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);

        Set<EntityType<?>> entities = sessionFactory.getMetamodel().getEntities();
        Map<String, HibernateBaseDao> beans = applicationContext.getBeansOfType(HibernateBaseDao.class);
        Map<Class<?>, HibernateBaseDao> classToBeans = new HashMap<>();
        for (HibernateBaseDao daoBean : beans.values()) {
            Type type = daoBean.getClass().getSuperclass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Class entityClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                classToBeans.put(entityClass, daoBean);
            }
        }

        for (EntityType<?> entityType : entities) {
            Class<?> clazz = entityType.getJavaType();
            if (ClassUtils.isAssignable(clazz, ActiveRecord.class)) {
                HibernateBaseDao daoBean = classToBeans.get(clazz);
                if (daoBean == null) {
                    throw new IllegalAccessException(clazz.getName() + " 没有配置dao bean");
                }
                Field field = FieldUtils.getField(clazz, "hibernateBaseDao", true);
                FieldUtils.writeStaticField(field, daoBean, true);

                field = FieldUtils.getField(clazz, "transactionTemplate", true);
                FieldUtils.writeStaticField(field, transactionTemplate, true);
            }
        }
    }
}
