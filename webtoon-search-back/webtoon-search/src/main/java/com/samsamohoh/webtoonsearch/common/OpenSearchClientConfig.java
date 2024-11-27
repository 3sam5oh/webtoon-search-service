package com.samsamohoh.webtoonsearch.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class OpenSearchClientConfig {

    @Value("aws.open-search.endpoint")
    private String endpoint;

    @Value("aws.open-search.id")
    private String id;

    @Value("aws.open-search.pwd")
    private String pwd;

    @Bean
    public OpenSearchClient openSearchClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(id, pwd)
        );

        RestClientBuilder builder = RestClient.builder(new HttpHost(endpoint, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback(){
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setDefaultIOReactorConfig(IOReactorConfig.custom()
                                        .setIoThreadCount(1)
                                        .build());
                    }
                });

        RestClient restClient = builder.build();
        return new OpenSearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper(new ObjectMapper())));
    }
}
