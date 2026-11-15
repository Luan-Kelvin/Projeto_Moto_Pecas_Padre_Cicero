package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokeDTO(@NotBlank String refreshToken) {
}
