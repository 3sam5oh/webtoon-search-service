//package com.samsamohoh.webtoonsearch.adapter.config;
//
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.opensearch.client.RestClient;
//import org.opensearch.client.json.jackson.JacksonJsonpMapper;
//import org.opensearch.client.opensearch.OpenSearchClient;
//import org.opensearch.client.transport.OpenSearchTransport;
//import org.opensearch.client.transport.rest_client.RestClientTransport;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SearchEngineConfig {
//
//    @Value("${spring.opensearch.uris}")
//    private String opensearchUri;
//
//    @Value("${spring.opensearch.connection-timeout}")
//    private int connectionTimeout;
//
//    @Value("${spring.opensearch.socket-timeout}")
//    private int socketTimeout;
//
//    @Bean
//    public OpenSearchClient openSearchClient() {
//        // 기본 인증 자격 증명 추가
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "Admin1234!@#$"));
//
//        HttpHost httpHost = HttpHost.create(opensearchUri);
//
//        RestClient restClient = RestClient.builder(httpHost)
//                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
//                        .setConnectTimeout(connectionTimeout)
//                        .setSocketTimeout(socketTimeout)
//                )
//                .build();
//
//        OpenSearchTransport transport = new RestClientTransport(
//                restClient,
//                new JacksonJsonpMapper()
//        );
//
//        return new OpenSearchClient(transport);
//    }
//}
