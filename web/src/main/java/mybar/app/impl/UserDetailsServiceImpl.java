package mybar.app.impl;

import mybar.api.users.IRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.api.users.IBasicUser;
import mybar.service.users.UserManagementService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Spring-security requires an implementation of UserDetailService. 
 */
@Service("userDetailsService")
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserManagementService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        IBasicUser domainUser = userService.findByUsername(username);

        if (domainUser == null) {
            throw new UsernameNotFoundException("UserAccount for name \""
                    + username + "\" not found.");
        }

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                domainUser.getLogin(),
                domainUser.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                getAuthorities((Collection<IRole>) domainUser.getRoles())
        );
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Collection<IRole> roles) {
        List<GrantedAuthority> authList = getGrantedAuthorities(roles);
        return authList;
    }

    public static List<GrantedAuthority> getGrantedAuthorities(Collection<IRole> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (IRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getWebRole().name()));
        }
        return authorities;
    }

}