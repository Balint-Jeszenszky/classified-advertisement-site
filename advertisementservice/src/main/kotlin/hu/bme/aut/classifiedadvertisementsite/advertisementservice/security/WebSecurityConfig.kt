package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun authenticationHeaderFilter(): AuthHeaderFilter? {
        return AuthHeaderFilter()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun configure(http: HttpSecurity, unauthorizedHandler: AuthEntryPoint?): SecurityFilterChain? {
        http.csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests()
            .requestMatchers(
                HttpMethod.GET,
                "/external/advertisement/*",
                "/external/advertisements"
            )
            .permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(authenticationHeaderFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}