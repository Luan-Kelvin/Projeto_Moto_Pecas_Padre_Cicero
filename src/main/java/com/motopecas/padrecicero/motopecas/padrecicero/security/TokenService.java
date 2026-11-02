package com.motopecas.padrecicero.motopecas.padrecicero.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.LoginDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.TokenResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.security.ErroGeneratingTokenException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.security.ErrorVerifyingTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.net.Authenticator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Autowired
    private AuthenticationManager authentication;

    @Value("${api.security.token.secret}")
    private String secret;


    // AUTENTICAR FUNCIONARIO NO LOGIN
    public TokenResponseDTO authenticate(LoginDTO dto){
        var auth = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticate = authentication.authenticate(auth);

        Employee employee = (Employee) authenticate.getPrincipal();

        String tokenAccess = generateToken(employee);
        String tokenRefresh = generateRefreshToken(employee);

        return new TokenResponseDTO(tokenAccess, tokenRefresh);
    }

    // GERAR TOKEN DE ACESSO
    private String generateToken(Employee employee){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Moto Peças Padre Cicero")
                    .withSubject(employee.getEmail())
                    .withExpiresAt(LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00")))
                    .withClaim("token_type", "access")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new ErroGeneratingTokenException("ERRO! não foi possível gerar token de acesso.");
        }
    }

    // VERIFICAR E VALIDAR TOKEN DE ACESSO
    public String validateTokenAccess(String tokenJWT){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API Moto Peças Padre Cicero")
                    .withClaim("token_type", "access")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();

        } catch (JWTVerificationException exception){
            throw new ErrorVerifyingTokenException("ERRO! Token enviado não passou na verificação.");
        }
    }


    // GERAR REFRESH TOKEN
    private  String generateRefreshToken(Employee employee){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Moto Peças Padre Cicero")
                    .withSubject(employee.getId().toString())
                    .withExpiresAt(LocalDateTime.now().plusMinutes(120).toInstant(ZoneOffset.of("-03:00")))
                    .withClaim("token_type", "refresh")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new ErroGeneratingTokenException("ERRO! não foi possível gerar refresh token.");
        }
    }

    // VERIFICAR E VALIDAR TOKEN DE ACESSO
    public String validateRefreshToken(String refreshToken){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API Moto Peças Padre Cicero")
                    .withClaim("token_type", "refresh")
                    .build()
                    .verify(refreshToken)
                    .getSubject();

        } catch (JWTVerificationException exception){
            throw new ErrorVerifyingTokenException("ERRO! Token enviado não passou na verificação.");
        }
    }


}
