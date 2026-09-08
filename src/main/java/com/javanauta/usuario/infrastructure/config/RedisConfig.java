package com.javanauta.usuario.infrastructure.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${redis.port}")
    private int porta;

    @Value("${redis.hostname}")
    private String hostname;

    @Value("${redis.password}")
    private String password;



    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){

        RedisStandaloneConfiguration serverConfiguration = new RedisStandaloneConfiguration();
        serverConfiguration.setHostName(hostname); //qual o host que rodando
        serverConfiguration.setPort(porta); //qual a porta
        serverConfiguration.setPassword(password); // qual a senha

        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(2000)) //timeout para abrir a conexão
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions) // finaliza essa config de timeout
                .build();

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions) //timeout para iniciar a conexao
                .commandTimeout(Duration.ofMillis(2000))  //timeout pra setar ou buscar os dados
                .shutdownTimeout(Duration.ofMillis(100))  // tempo que permanece conectado depois que a api fecha
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfiguration, clientConfiguration);

        factory.afterPropertiesSet();

        return factory;
    }
}
