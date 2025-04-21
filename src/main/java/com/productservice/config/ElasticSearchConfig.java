package com.productservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.host}")
    private String elasticHost;
    @Value("${elasticsearch.port}")
    private int elasticPort;

    private RestClient restClient;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        restClient = RestClient.builder(
                new HttpHost(elasticHost, elasticPort)
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }

    @PreDestroy
    public void close() {
        try {
            if (restClient != null) {
                restClient.close();
            }
        } catch (Exception e) {
            log.error("ElasticsearchClient pre destroy exception: {}",e.getMessage());
        }
    }

}
