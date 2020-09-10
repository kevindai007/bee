package cn.tongdun.bee.core.enums.mvc;

import cn.tongdun.bee.core.enums.BaseIntegerEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by libinsong on 2020/9/9 9:37 下午
 */
public class IntegerToEnumConverterFactory implements ConverterFactory<Integer, BaseIntegerEnum> {

    private static final Map<Class, Converter> CONVERTERS = new HashMap<>();

    /**
     * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型，有多个实现
     *
     * @param targetType 转换后的类型
     * @return 返回一个转化器
     */
    @Override
    public <T extends BaseIntegerEnum> Converter<Integer, T> getConverter(Class<T> targetType) {
        Converter<Integer, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IntegerToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
