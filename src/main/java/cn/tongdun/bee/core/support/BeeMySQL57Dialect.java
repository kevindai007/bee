package cn.tongdun.bee.core.support;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DateType;

/**
 * Created by binsong.li on 2018/6/6 下午1:06
 */
public class BeeMySQL57Dialect extends MySQL57Dialect {

    public BeeMySQL57Dialect() {
        super();
        registerFunction( "date_sub_interval",
            new SQLFunctionTemplate(DateType.INSTANCE, "date_sub(?1, INTERVAL ?2 ?3)" ) );
        registerFunction( "date_add_interval",
            new SQLFunctionTemplate(DateType.INSTANCE, "date_add(?1, INTERVAL ?2 ?3)" ) );
    }
}
