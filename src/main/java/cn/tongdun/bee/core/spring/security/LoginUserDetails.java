package cn.tongdun.bee.core.spring.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * Created by admin on 2017/3/9.
 */
public class LoginUserDetails implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private static final Log logger = LogFactory.getLog(org.springframework.security.core.userdetails.User.class);

    // ~ Instance fields
    // ================================================================================================
    private String password;
    private final String username;
    private final String cnName;
    private final boolean admin;
    private final String email;
    private final Set<GrantedAuthority> authorities;
    private final int loginAttempts; // 登录失败尝试次数
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean credentialsNonUpdatePW; // 系统分配默认账号，或者重置密码，首次登录需要强制修改密码。false 需要更新密码，true 不需要更新密码
    private final boolean accountConfirm; // 账号是否验证通过
    private final boolean enabled;


    // ~ Constructors
    // ===================================================================================================

    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     */
    public LoginUserDetails(String username, String password, String cnName, boolean admin, String email,
                            Collection<? extends GrantedAuthority> authorities) {
        this(username, password, cnName, admin, email, true, true, true, true, true, true, 0, authorities);
    }

    public LoginUserDetails(String username, String password, String cnName, boolean admin, String email,
                boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                boolean accountNonLocked, boolean credentialsNonUpdatePW, boolean accountConfirm,
                int loginAttempts, Collection<? extends GrantedAuthority> authorities) {

        if (((username == null) || "".equals(username)) || (password == null)) {
            throw new IllegalArgumentException(
                    "Cannot pass null or empty values to constructor");
        }

        this.username = username;
        this.password = password;
        this.cnName = cnName;
        this.admin = admin;
        this.email = email;
        this.enabled = enabled;
        this.loginAttempts = loginAttempts;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonUpdatePW = credentialsNonUpdatePW;
        this.accountConfirm = accountConfirm;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }

    // ~ Methods
    // ========================================================================================================

    public String getFullName() {
        return this.getCnName() + "#" + this.getUsername();
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getCnName() {
        return cnName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void eraseCredentials() {
        password = null;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public boolean isCredentialsNonUpdatePW() {
        return credentialsNonUpdatePW;
    }

    public boolean isAccountConfirm() {
        return accountConfirm;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>,
            Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof LoginUserDetails) {
            return username.equals(((LoginUserDetails) rhs).username);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("CnName: ").append(this.cnName).append("; ");
        sb.append("Email: ").append(this.email).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
                .append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        }
        else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }

    /**
     * Creates a UserBuilder with a specified user name
     *
     * @param username the username to use
     * @return the UserBuilder
     */
    public static UserBuilder withUsername(String username) {
        return builder().username(username);
    }

    /**
     * Creates a UserBuilder
     *
     * @return the UserBuilder
     */
    public static UserBuilder builder() {
        return new LoginUserDetails.UserBuilder();
    }

    @Deprecated
    public static UserBuilder withDefaultPasswordEncoder() {
        logger.warn("User.withDefaultPasswordEncoder() is considered unsafe for production and is only intended for sample applications.");
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return builder().passwordEncoder(encoder::encode);
    }

    public static UserBuilder withUserDetails(LoginUserDetails userDetails) {
        return withUsername(userDetails.getUsername())
                .password(userDetails.getPassword())
                .cnName(userDetails.getCnName())
                .email(userDetails.getEmail())
                .loginAttempts(userDetails.getLoginAttempts())
                .accountNonExpired(!userDetails.isAccountNonExpired())
                .accountNonLocked(!userDetails.isAccountNonLocked())
                .authorities(userDetails.getAuthorities())
                .credentialsNonExpired(!userDetails.isCredentialsNonExpired())
                .credentialsNonUpdatePW(userDetails.isCredentialsNonUpdatePW())
                .accountConfirm(userDetails.isAccountConfirm())
                .enabled(!userDetails.isEnabled());
    }

    public static class UserBuilder {
        private String username;
        private String password;
        private String cnName;
        private boolean admin;
        private String email;
        private List<GrantedAuthority> authorities;
        private int loginAttempts; // 登录失败尝试次数
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean credentialsNonUpdatePW; // 系统分配默认账号，或者重置密码，首次登录需要强制修改密码
        private boolean accountConfirm; // 账号是否验证通过
        private boolean enabled;

        private Function<String, String> passwordEncoder = password -> password;

        /**
         * Creates a new instance
         */
        private UserBuilder() {
        }

        public UserBuilder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            Assert.notNull(password, "password cannot be null");
            this.password = password;
            return this;
        }

        public UserBuilder cnName(String cnName) {
            Assert.notNull(cnName, "cnName cannot be null");
            this.cnName = cnName;
            return this;
        }

        public UserBuilder cnName(boolean admin) {
            Assert.notNull(cnName, "admin cannot be null");
            this.admin = admin;
            return this;
        }

        public UserBuilder email(String email) {
            Assert.notNull(email, "email cannot be null");
            this.email = email;
            return this;
        }

        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            Assert.notNull(encoder, "encoder cannot be null");
            this.passwordEncoder = encoder;
            return this;
        }

        public UserBuilder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(
                    roles.length);
            for (String role : roles) {
                Assert.isTrue(!role.startsWith("ROLE_"), () -> role
                        + " cannot start with ROLE_ (it is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return authorities(authorities);
        }

        public UserBuilder authorities(GrantedAuthority... authorities) {
            return authorities(Arrays.asList(authorities));
        }

        public UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        public UserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        public UserBuilder loginAttempts(int loginAttempts) {
            this.loginAttempts = loginAttempts;
            return this;
        }

        public UserBuilder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public UserBuilder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public UserBuilder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public UserBuilder credentialsNonUpdatePW(boolean credentialsNonUpdatePW) {
            this.credentialsNonUpdatePW = credentialsNonUpdatePW;
            return this;
        }

        public UserBuilder accountConfirm(boolean accountConfirm) {
            this.accountConfirm = accountConfirm;
            return this;
        }

        public UserBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(password);
            return new LoginUserDetails(username, encodedPassword, cnName, admin, email, enabled,
                    accountNonExpired, credentialsNonExpired, accountNonLocked, credentialsNonUpdatePW,
                    accountConfirm, loginAttempts, authorities);
        }
    }
}
