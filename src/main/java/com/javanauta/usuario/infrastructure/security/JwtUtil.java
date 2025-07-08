package com.javanauta.usuario.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    // Chave secreta usada para assinar e verificar tokens JWT
    @Value("${security.apiKey}")
    private String base64SecretKey;

    // Gera uma Key a partir da chave secreta String codificada em Base64
    private SecretKey getSigningKey() {
        // Decodifica a chave secreta em Base64 padrão e cria uma SecretKey
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Gera um token JWT com o nome de usuário e validade de 1 hora
    public String generateToken(String username) {
        return Jwts.builder() // Inicia o processo de construção do token JWT
                .subject(username) // Define o nome de usuário como o "subject" do token
                .issuedAt(new Date()) // Define a data e hora atuais como o momento de emissão do token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 600 * 600)) // Define a data e hora de expiração do token para 1 hora a partir da emissão
                .signWith(getSigningKey()) // Assina o token usando a chave de assinatura fornecida pelo método getSigningKey()
                .compact(); // Conclui a construção do token e retorna o token compactado como uma String
    }

    // Extrai as claims do token JWT (informações adicionais do token)
    private Claims extractClaims(String token) {
        return Jwts.parser() // Inicia o processo de parsing do token JWT
                .verifyWith(getSigningKey()) // Configura o parser para verificar a assinatura do token usando a chave de assinatura fornecida
                .build() // Conclui a configuração do parser
                .parseSignedClaims(token) // Faz o parsing do token e extrai as claims assinadas
                .getPayload(); // Obtém o payload (corpo) do token, que contém as claims
    }

    // Extrai o email do usuário do token JWT
    public String extrairEmailToken(String token) {
        // Obtém o assunto (nome de usuário) das claims do token
        return extractClaims(token).getSubject();
    }

    // Verifica se o token JWT está expirado
    public boolean isTokenExpired(String token) {
        // Compara a data de expiração do token com a data atual
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valida o token JWT verificando o nome de usuário e se o token não está expirado
    public boolean validateToken(String token, String username) {
        // Extrai o nome de usuário do token
        final String extractedUsername = extrairEmailToken(token);
        // Verifica se o nome de usuário do token corresponde ao fornecido e se o token não está expirado
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
