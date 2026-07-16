package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.ApiAccessDeniedHandler;
import ar.edu.itba.paw.webapp.auth.ApiAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.auth.JwtAuthenticationFilter;
import ar.edu.itba.paw.webapp.auth.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@EnableWebSecurity
@Configuration
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
public class WebAuthConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Link", "Location", "ETag", "Total-Elements"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Configuration
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

        @Autowired
        private ApiAccessDeniedHandler apiAccessDeniedHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**")
                    .userDetailsService(userDetailsService)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().authorizeRequests()
                        .antMatchers(OPTIONS, "/api/**").permitAll()
                        .antMatchers(GET, "/api/users/me").authenticated()
                        .antMatchers(GET, "/api/**").permitAll()
                        .antMatchers(POST,
                                "/api/users",
                                "/api/users/sessions",
                                "/api/users/verifications",
                                "/api/users/password-requests").permitAll()
                        .antMatchers(PUT,
                                "/api/users/verifications",
                                "/api/users/password-requests").permitAll()
                        .antMatchers(POST,
                                "/api/tournaments",
                                "/api/tournaments/*/participants/users",
                                "/api/tournaments/*/participants/teams",
                                "/api/users/me/accounts",
                                "/api/teams").hasRole("VERIFIED")
                        .antMatchers(PUT,
                                "/api/tournaments/*",
                                "/api/tournaments/*/status",
                                "/api/tournaments/*/matches/*/results",
                                "/api/teams/*").hasRole("VERIFIED")
                        .antMatchers(DELETE,
                                "/api/tournaments/*/participants/**",
                                "/api/users/me/accounts/*").hasRole("VERIFIED")
                        .anyRequest().authenticated()
                    .and().exceptionHandling()
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler)
                    .and().formLogin().disable()
                        .httpBasic().disable()
                        .rememberMe().disable()
                        .logout().disable()
                        .requestCache().disable()
                        .csrf().disable()
                        .cors();
        }
    }

    @Configuration
    public static class MvcSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

        @Value("classpath:rememberme.key")
        private Resource rememberMeKey;

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.userDetailsService(userDetailsService)
                    .sessionManagement()
                        .invalidSessionUrl("/")
                    .and().authorizeRequests()
                        .antMatchers("/login", "/register").anonymous()
                        .antMatchers("/", "/verify", "/verify/confirm").permitAll()
                        .antMatchers("/tournament/update",
                                "/tournament/startTournament",
                                "/tournament/removeParticipant",
                                "/tournament/swap/matches",
                                "/tournament/swap/groups",
                                "/tournament/closeInscriptions",
                                "/tournament/setWinner",
                                "/tournament/setMatchResults")
                            .access("@tournamentSecurity.isCreator(authentication, request)")
                        .antMatchers("/tournaments/new/step1",
                                "/tournaments/new/step2",
                                "/tournaments/new/step3",
                                "/tournament/join",
                                "/myTournaments",
                                "/profile/update",
                                "/tournament/leave",
                                "/tournament/join/step1",
                                "/tournament/join/step2",
                                "/team/create",
                                "/team/update",
                                "/tournament/contactOwner",
                                "/tournament/rate",
                                "/profile/{id}/comment").hasRole("VERIFIED")
                    .and().formLogin()
                        .defaultSuccessUrl("/", false)
                        .usernameParameter("j_username")
                        .passwordParameter("j_password")
                        .loginPage("/login")
                    .and().rememberMe()
                        .rememberMeParameter("j_rememberme")
                        .key(readRememberMeKey())
                        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                    .and().logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                    .and().exceptionHandling()
                        .accessDeniedPage("/403")
                    .and().csrf().disable()
                        .cors();
        }

        private String readRememberMeKey() {
            try {
                return new String(rememberMeKey.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                    .antMatchers("/css/**", "/js/**", "/images/**", "favicon.ico", "/fonts/**");
        }
    }
}
