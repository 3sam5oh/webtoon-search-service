package com.samsamohoh.webtoonsearch.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.uris}")
    private String opensearchUri;

    @Value("${opensearch.connection-timeout}")
    private String connectionTimeout;

    @Value("${opensearch.socket-timeout}")
    private String socketTimeout;

    @Bean
    public OpenSearchClient openSearchClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(opensearchUri))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(Duration.parse(connectionTimeout).toMillisPart())
                        .setSocketTimeout(Duration.parse(socketTimeout).toMillisPart())
                )
                .build();

        OpenSearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new OpenSearchClient(transport);
    }
}
