package eu.dickovadev.pojisteniapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class ApplicationSecurityConfiguration {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    public ApplicationSecurityConfiguration(
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler
    ) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                // Public pages:
                                .requestMatchers(
                                        "/account/login",
                                        "/account/logout",
                                        "/account/register",
                                        "/account/change-password",
                                        "/access-denied",
                                        "/error",
                                        "/404",
                                        "/",
                                        "/about",
                                        "/event-info",
                                        "/policy-info"
                                ).permitAll()
                                .requestMatchers(
                                        "/static/**",
                                        "/icons/**",
                                        "/css/**",
                                        "/js/**",
                                        "/images/**"
                                ).permitAll()
                                // All other requests require authentication
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/account/login")
                                .loginProcessingUrl("/account/login")
                                .defaultSuccessUrl("/", true)
                                .usernameParameter("email")
                                .successHandler(customAuthenticationSuccessHandler)
                                .failureHandler(customAuthenticationFailureHandler)
                                .permitAll() // Allow public access to login
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedPage("/access-denied")
                )
                .logout(logout ->
                        logout.logoutUrl("/account/logout")
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
