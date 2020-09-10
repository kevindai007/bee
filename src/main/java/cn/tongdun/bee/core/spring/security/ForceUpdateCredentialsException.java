package cn.tongdun.bee.core.spring.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by libinsong on 2020/8/7 1:41 下午
 */
public class ForceUpdateCredentialsException extends AuthenticationException {

    public ForceUpdateCredentialsException(String msg) {
        super(msg);
    }

    public ForceUpdateCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }
}
