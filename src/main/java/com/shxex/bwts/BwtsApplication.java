package com.shxex.bwts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author ljp
 */
@SpringBootApplication
@EnableElasticsearchRepositories
public class BwtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BwtsApplication.class, args);
    }

}
