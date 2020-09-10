package cn.tongdun.bee.core.enums.jackson;

import cn.tongdun.bee.core.enums.BaseStringEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Created by libinsong on 2020/9/9 10:07 下午
 */
public class JacksonEnumStringSerializer extends StdSerializer<BaseStringEnum> {

    public JacksonEnumStringSerializer() {
        super(BaseStringEnum.class);
    }

    public JacksonEnumStringSerializer(Class<BaseStringEnum> t) {
        super(t);
    }

    @Override
    public void serialize(BaseStringEnum baseStringEnum, JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {

        gen.writeString(baseStringEnum.getValue());
    }
}
