package scrum.cannia.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import scrum.cannia.security.LoginSuccessHandler;

@AllArgsConstructor
@Configuration
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/inicio",
                                "/login",
                                "/registro/**",
                                "/css/**",
                                "/js/**",
                                "/img/**"

                        ).permitAll()


                        // ===== PAGO =====
                        .requestMatchers("/pago/**").hasRole("PROPIETARIO")
                        // ===== TIENDA =====
                        .requestMatchers("/carrito/**").hasRole("PROPIETARIO")
                        .requestMatchers("/tienda/propietario/**").hasRole("PROPIETARIO")
                        .requestMatchers("/tienda/veterinario/**").hasRole("VETERINARIO")

                        // ===== RUTAS POR ROL =====
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/propietario/**").hasRole("PROPIETARIO")
                        .requestMatchers("/veterinario/**").hasRole("VETERINARIO")
                        .requestMatchers("/fundacion/**").hasRole("FUNDACION")

                        .requestMatchers("/verificacion/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}


