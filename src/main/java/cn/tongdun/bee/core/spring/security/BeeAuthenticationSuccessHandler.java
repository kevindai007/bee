package cn.tongdun.bee.core.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by libinsong on 2020/8/27 10:47 下午
 */
public class BeeAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private BeeUserDetailsService userDetailsService;


    public BeeAuthenticationSuccessHandler(BeeUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {

        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
        userDetailsService.unlockUser(userDetails.getUsername());

        super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }
}
