package org.lemanoman.contas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


public class UserDetailsServiceImp implements UserDetailsService {
    private DatabaseService databaseService;

    public UserDetailsServiceImp(DatabaseService databaseService){
        this.databaseService = databaseService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    /*Here we are using dummy data, you need to load user data from
     database or other third party application*/
        UserModel user = findUserbyName(username);

        UserBuilder builder = null;
        if (user != null) {
            builder = User.withUsername(username);
            builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
            builder.roles(user.getRoles());
        } else {
            throw new UsernameNotFoundException("UserModel not found.");
        }

        return builder.build();
    }

    private UserModel findUserbyName(String username) {
        return databaseService.getUsuario(username);
    }
}
