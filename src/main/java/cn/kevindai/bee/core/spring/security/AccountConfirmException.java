package cn.kevindai.bee.core.spring.security;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by libinsong on 2020/8/7 1:33 下午
 */
public class AccountConfirmException extends AccountStatusException {

    public AccountConfirmException(String msg) {
        super(msg);
    }

    public AccountConfirmException(String msg, Throwable t) {
        super(msg, t);
    }
}
