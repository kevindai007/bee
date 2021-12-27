package cn.kevindai.bee.core.enums;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * 参考：http://www.gabiaxel.com/2011/01/better-enum-mapping-with-hibernate.html
 *
 * Created by libinsong on 2020/7/19 6:14 下午
 */
public class StringValuedEnumType<T extends Enum<T> & BaseStringEnum> implements UserType, ParameterizedType {

    private Class<T> enumClass;

    private String defaultValue;

    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = (Class<T>) Class.forName(enumClassName).asSubclass(Enum.class).
                    asSubclass(BaseStringEnum.class); //Validates the class but does not eliminate the cast
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }

        setDefaultValue(parameters.getProperty("defaultValue"));
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Class<T> returnedClass() {
        return enumClass;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {

        String value = rs.getString(names[0]);
        if (rs == null) {
            value = getDefaultValue();
            if (value == null) { //no default value
                return null;
            }
        }
        for(BaseStringEnum stringEnum : returnedClass().getEnumConstants()) {
            if (StringUtils.equals(value, stringEnum.getValue())) {
                return value;
            }
        }
        throw new IllegalStateException("Unknown " + returnedClass().getSimpleName() + " code " + value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, ((BaseStringEnum) value).getValue());
        }
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }
}
