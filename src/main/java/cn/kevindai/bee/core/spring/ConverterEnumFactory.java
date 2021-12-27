package cn.kevindai.bee.core.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实现 Spring MVC 转换枚举类型
 * 使用枚举类型需要实现 Converter Enum 接口
 * String param() -> Enum
 *
 * https://gitee.com/Devifish/spring-converter-enum
 *
 * Created by admin on 2019/12/12 9:19 上午
 */
@SuppressWarnings("unchecked")
public class ConverterEnumFactory implements ConverterFactory<String, ConvertibleEnum<?>> {

    private static final String NULL_KEY = "NULL PARAM";
    private static final Map<String, Converter<String, ConvertibleEnum<?>>> convertMap = new HashMap<>();

    /**
     * 获取 对应枚举类型的 转换器
     * @param c 枚举 Class对象
     * @param <E> 枚举
     * @return 转换器
     */
    @Override
    public <E extends ConvertibleEnum<?>> Converter<String, E> getConverter(Class<E> c) {
        if (c == null) return null;

        String name = c.getName();
        if (!convertMap.containsKey(name))
            convertMap.put(name, Stream.of(c.getEnumConstants()).collect(Collectors.toMap(this::toString, e -> e))::get);
        return (Converter<String, E>) convertMap.get(name);
    }

    /**
     * 防止 空字符串 碰撞 Map空键
     * @param object 枚举
     * @return param key
     */
    private String toString(ConvertibleEnum<?> object) {
        Serializable param = object.param();
        return param != null ? param.toString(): NULL_KEY;
    }

}
