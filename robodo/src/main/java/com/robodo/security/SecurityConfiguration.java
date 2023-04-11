package com.robodo.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.robodo.services.ProcessService;
import com.robodo.ui.UILogin;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity 
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	
	@Autowired
	ProcessService processService;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/public/**"))
            .permitAll();

        super.configure(http); 

        setLoginView(http, UILogin.class); 
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean
    public UserDetailsManager userDetailsService() {
    	
    	if (processService.getEnv().getProperty("spring.jpa.hibernate.ddl-auto").equals("create")) {
    		UserDetails user =
                    User.withUsername("user")
                            .password("{noop}123")
                            .roles("USER")
                            .build();
            UserDetails admin =
                    User.withUsername("admin")
                            .password("{noop}admin123")
                            .roles("ADMIN","USER")
                            .build();
            return new InMemoryUserDetailsManager(user, admin);
    	}
        
        
    	var userDetailManager = new InMemoryUserDetailsManager();

    	List<com.robodo.model.User> activeUsers = processService.getActiveUsers();
    	//System.err.println("size : %d".formatted(activeUsers.size()));
        for (var user : activeUsers) {
        	//System.err.println("adding user %s with password %s".formatted(user.getUsername(), user.getPassword()));
        	userDetailManager.createUser(user.asUserDetails(processService));
        }
        
        
        return userDetailManager;
        
        
    }


}
