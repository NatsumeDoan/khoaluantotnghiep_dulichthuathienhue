package com.husc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.husc.security.jwt.AuthEntryPointJwt;
import com.husc.security.jwt.AuthTokenFilter;
import com.husc.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }
  
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }
  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeHttpRequests().requestMatchers("/auth/**").permitAll()
        .requestMatchers("/place/**").permitAll()
        .requestMatchers("/admin/place/**").hasRole("ADMIN")
        .requestMatchers("/food/**").permitAll()
        .requestMatchers("/admin/food/**").hasRole("ADMIN")
        .requestMatchers("/admin/hotel/**").hasRole("ADMIN")
        .requestMatchers("/hotel/**").permitAll()
        .requestMatchers("/tour/**").permitAll()
        .requestMatchers("/admin/tour/**").hasAnyRole("ADMIN","MODERATOR")
        .requestMatchers("/category/**").hasRole("ADMIN")
        .requestMatchers("/user/**").permitAll()
        .requestMatchers("/admin/user/**").hasRole("ADMIN")
        .requestMatchers("https://cdn.ckeditor.com/**").permitAll()
        .requestMatchers("/richtext/**").permitAll()
        .requestMatchers("/paypal/**").permitAll()
        .requestMatchers("/booking/**").permitAll()
        .requestMatchers("/admin/booking/**").hasAnyRole("ADMIN","MODERATOR")
        .requestMatchers("/admin//**").hasRole("ADMIN")
        .requestMatchers("/reviewfoods/**").permitAll()
        .requestMatchers("/commenttours/**").permitAll()
        .requestMatchers("/reviewhotels/**").permitAll()
        .requestMatchers("/comments/**").permitAll()
        .requestMatchers("/image/**").permitAll()
        .anyRequest().authenticated().and().formLogin().loginPage("/auth/formsignin").permitAll()
        .and().logout().deleteCookies("jwtToken").logoutSuccessUrl("/auth/formsignin").permitAll();
    
    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }
}