package cn.kevindai.bee.core.enums.jackson;

import cn.kevindai.bee.core.enums.BaseIntegerEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Created by libinsong on 2020/9/9 10:07 下午
 */
public class JacksonEnumIntegerSerializer extends StdSerializer<BaseIntegerEnum> {

    public JacksonEnumIntegerSerializer() {
        super(BaseIntegerEnum.class);
    }

    public JacksonEnumIntegerSerializer(Class<BaseIntegerEnum> t) {
        super(t);
    }

    @Override
    public void serialize(BaseIntegerEnum baseStringEnum, JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {

        gen.writeNumber(baseStringEnum.getValue());
    }
}
