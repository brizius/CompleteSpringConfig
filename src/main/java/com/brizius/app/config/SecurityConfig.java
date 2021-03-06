package com.brizius.app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.social.UserIdSource;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.brizius.app.security.SimpleSocialUsersDetailService;


@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private ApplicationContext context;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private Environment env;

  @Autowired
  public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource)
        .usersByUsernameQuery("select username, password, true from User where username = ?")
        .authoritiesByUsernameQuery("select username, 'ROLE_USER' from User where username = ?")
        .passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/resources/**");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    // .csrf().disable()
    .formLogin()
        .loginPage("/signin")
        .loginProcessingUrl("/signin/authenticate")
        .failureUrl("/signin?param.error=bad_credentials")
        .and()
        .logout()
        .logoutUrl("/signout")
        .deleteCookies("JSESSIONID")
        .and()
        .authorizeRequests()
        .antMatchers("/admin/**", "/favicon.ico", "/resources/**", "/auth/**", "/signin/**",
            "/signup/**", "/disconnect/facebook").permitAll().antMatchers("/**").authenticated()

        .and().requiresChannel().anyRequest().requiresSecure()

        /*
         * .and() .rememberMe()
         */
        .and().apply(new SpringSocialConfigurer());
  }

  @Bean
  public RequestDataValueProcessor requestDataValueProcessor() {
    return new CsrfRequestDataValueProcessor();
  }

  @Bean
  public SocialUserDetailsService socialUsersDetailService() {
    return new SimpleSocialUsersDetailService(userDetailsService());
  }

  @Bean
  public UserIdSource userIdSource() {
    return new AuthenticationNameUserIdSource();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
