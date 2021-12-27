package cn.kevindai.bee.core.enums.mvc;

import cn.kevindai.bee.core.enums.BaseIntegerEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * 参考: https://xkcoding.com/2019/01/30/spring-boot-request-use-enums-params.html
 * Created by libinsong on 2020/9/9 9:33 下午
 */
public class IntegerToEnumConverter<T extends BaseIntegerEnum> implements Converter<Integer, T> {

    private Map<Integer, T> enumMap = new HashMap<>();

    public IntegerToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getValue(), e);
        }
    }

    @Override
    public T convert(Integer source) {
        T t = enumMap.get(source);
        if (t == null) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型: " + source);
        }
        return t;
    }
}
