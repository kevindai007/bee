package cn.tongdun.bee.core.spring.security;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by zheng on 2019-09-19.
 */
public class UserNameAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public static final String LAST_USERNAME_KEY = "LAST_USERNAME";

    private String loginUrl;

    private String changePasswordUrl;

    public UserNameAuthenticationFailureHandler(String loginUrl, String changePasswordUrl) {
        super("/");
        this.loginUrl = loginUrl;
        this.changePasswordUrl = changePasswordUrl;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        if (exception instanceof ForceUpdateCredentialsException ||
                exception instanceof AccountExpiredException) {
            this.setDefaultFailureUrl(changePasswordUrl);
        } else {
            this.setDefaultFailureUrl(loginUrl);
        }
        
        super.onAuthenticationFailure(request, response, exception);

        String lastUserName = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);

        HttpSession session = request.getSession(false);
        if (session != null || isAllowSessionCreation()) {
            request.getSession().setAttribute(LAST_USERNAME_KEY, lastUserName);
        }
    }
}
