package com.motopecas.padrecicero.motopecas.padrecicero.security;

import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeNoFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeRepository.findByEmail(email)
                .orElseThrow(
                        () -> new EmployeeNoFoundException(String.format("ERRO! Funcionário com email: %s não foi encontrado."))
                );
    }
}
