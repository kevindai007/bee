package cn.tongdun.bee.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by libinsong on 2017/3/9.
 */
public class LoginUserDetails extends User {

    /**
     * 中文名称
     */
    private String cnName;

    public LoginUserDetails(String username, String password,
                            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public LoginUserDetails(String username, String cnName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.cnName = cnName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }
}
