package com.samsamohoh.webtoonsearch.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 로컬 개발 환경을 위한 OpenSearch 설정
 * k3d와 기반 springboot helm으로 구축된 OpenSearch 연결 담당
 */
@Configuration
public class TestOpenSearchClientConfig {
    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.port}")
    private int port;

    @Value("${opensearch.protocol}")
    private String protocol;

    @Value("${opensearch.connection-timeout}")
    private int connectionTimeout;

    @Value("${opensearch.socket-timeout}")
    private int socketTimeout;

    @Value("${opensearch.security.username}")
    private String username;

    @Value("${opensearch.security.password}")
    private String password;

    @Bean
    public OpenSearchClient openSearchClient() {
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                )
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setDefaultIOReactorConfig(
                                        IOReactorConfig.custom()
                                                .setIoThreadCount(1)
                                                .setSoTimeout(socketTimeout)
                                                .setConnectTimeout(connectionTimeout)
                                                .build()
                                )
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                )
                .build();

        return new OpenSearchClient(
                new RestClientTransport(
                        restClient,
                        new JacksonJsonpMapper(new ObjectMapper())
                )
        );
    }
}
