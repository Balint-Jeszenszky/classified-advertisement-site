package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class UserDetailsImpl(
    private var id: Int,
    private var username: String,
    private var email: String,
    private var enabled: Boolean,
    roles: List<String?>
) : UserDetails {
    private var authorities: Collection<GrantedAuthority>? = null

    init {
        val authorities = roles.map { role: String? ->
            SimpleGrantedAuthority(role)
        }.toList()
        this.authorities = authorities
    }

    fun getId(): Int {
        return id
    }

    fun getEmail(): String {
        return email
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}