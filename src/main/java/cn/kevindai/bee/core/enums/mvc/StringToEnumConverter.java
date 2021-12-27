package cn.kevindai.bee.core.enums.mvc;

import cn.kevindai.bee.core.enums.BaseStringEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by libinsong on 2020/9/9 9:41 下午
 */
public class StringToEnumConverter<T extends BaseStringEnum> implements Converter<String, T> {

    private Map<String, T> enumMap = new HashMap<>();

    public StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getValue(), e);
        }
    }

    @Override
    public T convert(String source) {
        T t = enumMap.get(source);
        if (t == null) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型: " + source);
        }
        return t;
    }
}
