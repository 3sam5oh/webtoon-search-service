package com.samsamohoh.webtoonsearch.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * 로컬 개발 환경을 위한 OpenSearch 설정
 * k3d와 기반 springboot helm으로 구축된 OpenSearch 연결 담당
 * SSL 검증을 비활성화하여 자체 서명된 인증서를 허용합니다.
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
    public OpenSearchClient openSearchClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // SSL 컨텍스트 생성 - 모든 인증서를 신뢰하도록 설정
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509Certificates, s) -> true)
                .build();

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
                                .setSSLContext(sslContext)  // SSL 컨텍스트 설정
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
