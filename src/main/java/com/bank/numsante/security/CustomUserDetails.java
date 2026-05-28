package com.bank.numsante.security;

import com.bank.numsante.entity.PersonnelMedical;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final PersonnelMedical personnel;

    public CustomUserDetails(PersonnelMedical personnel) {
        this.personnel = personnel;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + personnel.getRole().toUpperCase()));
    }

    @Override
    public String getPassword() { return personnel.getMotDePasseHash(); }

    @Override
    public String getUsername() { return personnel.getIdentifiantPro(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return personnel.getEstActif(); }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return personnel.getEstActif(); }

    public PersonnelMedical getPersonnel() { return personnel; }
}