package cn.kevindai.bee.core.spring;

import java.io.Serializable;

/**
 * 用于实现 Spring ConverterFactory 与 JPA AttributeConverter接口
 * 通过 param 实现到 Enum 的转换
 *
 * Created by admin on 2019/12/12 9:18 上午
 */
public interface ConvertibleEnum<V extends Serializable> {

    V param();

}
