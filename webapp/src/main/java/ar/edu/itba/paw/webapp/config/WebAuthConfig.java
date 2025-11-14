package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@Configuration
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("classpath:rememberme.key")
    private Resource rememberMeKey;

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
                    "/tournament/swap/matches",
                    "/tournament/swap/groups",
                    "/tournament/closeInscriptions",
                    "/tournament/setWinner")
            .access("@tournamentSecurity.isCreator(authentication, request)")
            .antMatchers("/tournaments/new/step1",
                    "/tournaments/new/step2",
                    "/tournament/join",
                    "/myTournaments",
                    "/profile/update",
                    "/tournament/leave",
                    "/tournament/join/step1",
                    "/tournament/join/step2",
                    "/team/create",
                    "team/update").hasRole("VERIFIED")
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
        .and().csrf().disable();
    }

    private String readRememberMeKey() {
        try {
            return new String(rememberMeKey.getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**", "/images/**", "favicon.ico", "/fonts/**");
    }
}
