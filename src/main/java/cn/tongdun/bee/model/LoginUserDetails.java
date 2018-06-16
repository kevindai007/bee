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

    private String email;

    private String appkey;

    public LoginUserDetails(String username, String password,
        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public LoginUserDetails(String username, String cnName, String password,
        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.cnName = cnName;
    }

    public String getFullName() {
        return this.getCnName() + "#" + this.getUsername();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

}