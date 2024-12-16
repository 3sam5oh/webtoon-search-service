//package com.samsamohoh.webtoonsearch.common;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpHost;
//import org.apache.http.impl.nio.reactor.IOReactorConfig;
//import org.opensearch.client.RestClient;
//import org.opensearch.client.json.jackson.JacksonJsonpMapper;
//import org.opensearch.client.opensearch.OpenSearchClient;
//import org.opensearch.client.transport.rest_client.RestClientTransport;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 로컬 개발 환경을 위한 OpenSearch 설정
// * Docker-Compose로 구축된 OpenSearch 연결 담당
// */
//@Configuration
////@Profile("local")
//public class LocalOpenSearchClientConfig {
//
//    @Value("${opensearch.host}")
//    private String host;
//
//    @Value("${opensearch.port}")
//    private int port;
//
//    @Value("${opensearch.connection-timeout}")
//    private int connectionTimeout;
//
//    @Value("${opensearch.socket-timeout}")
//    private int socketTimeout;
//
//    @Bean
//    public OpenSearchClient openSearchClient() {
//        RestClient restClient = RestClient.builder(new HttpHost(host, port))
//                .setRequestConfigCallback(requestConfigBuilder ->
//                        requestConfigBuilder
//                                .setConnectTimeout(connectionTimeout)
//                                .setSocketTimeout(socketTimeout)
//                )
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder
//                                .setDefaultIOReactorConfig(
//                                        IOReactorConfig.custom()
//                                                .setIoThreadCount(1)
//                                                .setSoTimeout(socketTimeout)
//                                                .setConnectTimeout(connectionTimeout)
//                                                .build()
//                                )
//                )
//                .build();
//
//        return new OpenSearchClient(
//                new RestClientTransport(
//                        restClient,
//                        new JacksonJsonpMapper(new ObjectMapper())
//                )
//        );
//    }
//}
