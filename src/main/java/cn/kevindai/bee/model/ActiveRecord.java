package cn.kevindai.bee.model;

import cn.kevindai.bee.core.hibernate5.ActiveRecordInitializer;
import cn.kevindai.bee.core.hibernate5.HibernateBaseDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * https://quarkus.io/guides/hibernate-orm-panache
 * https://github.com/quarkusio/quarkus/blob/c6fa955df69d43b33bee5b153a7030fc9f630094/extensions/panache/hibernate-orm-panache/runtime/src/main/java/io/quarkus/hibernate/orm/panache/PanacheEntityBase.java
 * Created by libinsong on 2020/9/11 10:17 下午
 */
public class ActiveRecord extends BaseEntity {

    private static HibernateBaseDao hibernateBaseDao;

    private static TransactionTemplate transactionTemplate;

    private static void checkActiveRecordEnv()  {
        if (hibernateBaseDao == null) {
            throw new RuntimeException("请把"
                    + ActiveRecordInitializer.class.getCanonicalName() + " 配置为spring bean");
        }
    }

    public Long save() {
        checkActiveRecordEnv();

        return transactionTemplate.execute(transactionStatus -> (Long) hibernateBaseDao.save(this));
    }

    public void update() {
        checkActiveRecordEnv();

        final Object that = this;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                hibernateBaseDao.update(that);
            }
        });
    }

    public void delete() {
        checkActiveRecordEnv();

        transactionTemplate.execute(transactionStatus -> (Long) hibernateBaseDao.delete(this));
    }

    public static void deleteById(Long id) {
        checkActiveRecordEnv();

        transactionTemplate.execute(transactionStatus -> (Long) hibernateBaseDao.delete(id));
    }

    public static <T extends ActiveRecord> T findById(Long id) {
        checkActiveRecordEnv();

        return transactionTemplate.execute(transactionStatus -> (T) hibernateBaseDao.get(id));
    }
}
