package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class LoggedInUserService {

    fun getLoggedInUser(): UserDetailsImpl? {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as UserDetailsImpl
    }

    fun isAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val authorities = authentication.authorities
        return authorities.map { a: GrantedAuthority? -> a?.authority }.contains("ROLE_ADMIN")
    }
}