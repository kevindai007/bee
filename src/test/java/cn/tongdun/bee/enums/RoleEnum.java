package cn.tongdun.bee.enums;

import cn.tongdun.bee.core.enums.BaseStringEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cn.tongdun.bee.core.enums.jackson.JacksonEnumStringSerializer;

/**
 * Created by libinsong on 2020/9/10 10:52 上午
 */
@JsonSerialize(using = JacksonEnumStringSerializer.class)
public enum RoleEnum implements BaseStringEnum {
    ADMIN("admin", "管理员"),
    USER("user", "普通用户");

    private String value;

    private String desc;

    RoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
