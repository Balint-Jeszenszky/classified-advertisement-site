package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                "/external/advertisements/*",
                "/external/advertisements/search/*",
                "/external/categories",
                "/external/advertisement/*/comments",
                "/external/category/*/search/**"
            )
            .permitAll()
            .requestMatchers(
                HttpMethod.HEAD,
                "/internal/**"
            )
            .permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(authenticationHeaderFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}