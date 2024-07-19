package com.samsamohoh.webtoonsearch.adapter.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchEngineConfig {

    @Value("${spring.opensearch.uris}")
    private String opensearchUri;

    @Value("${spring.opensearch.connection-timeout}")
    private int connectionTimeout;

    @Value("${spring.opensearch.socket-timeout}")
    private int socketTimeout;

    @Bean
    public OpenSearchClient openSearchClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(opensearchUri))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(connectionTimeout)
                        .setSocketTimeout(socketTimeout)
                )
                .build();

        OpenSearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new OpenSearchClient(transport);
    }
}
