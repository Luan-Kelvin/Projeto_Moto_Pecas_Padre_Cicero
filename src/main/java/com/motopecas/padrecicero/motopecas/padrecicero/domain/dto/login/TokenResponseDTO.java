package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login;

public record TokenResponseDTO(
        String tokenAccess,
        String TokenRefresh
) {
}
