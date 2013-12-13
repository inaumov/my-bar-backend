package mybar.app.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import mybar.app.AuthenticationService;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Specific for Spring Security.
     * This alias is declared in spring-security.xml.
     */
    @Resource(name = "authenticationManager")
    private AuthenticationManager authenticationManager;

    @Override
    public boolean login(String username, String password) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authenticate.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                return true;
            }
        } catch (AuthenticationException e) {
        }
        return false;
    }

    @Override
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
}