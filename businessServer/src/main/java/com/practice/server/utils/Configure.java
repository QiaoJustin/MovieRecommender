package com.practice.server.utils;

import com.mongodb.MongoClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

/**
 * @Description 通过 Configure类来实例化 Bean
 * @Author fuchen
 * @Date 2019/12/10 9:09
 * Version 1.0
 */
@Configuration
public class Configure {

    private String jedisHost;
    private String mongoHost;
    private int mongoPort;
    private String esClusterName;
    private String esHost;
    private int esPort;

    public Configure() throws IOException {
        // 一般如果配到配置可以使用 apache-commons 项目中 apache-Configuration

        // 加载配置文件
        Properties properties = new Properties();
        Resource resource = new ClassPathResource("application.properties");
        // 具体加载了配置文件
        properties.load(new FileInputStream(resource.getFile()));

        // 提取配置属性
        this.jedisHost = properties.getProperty("jedis.host");
        this.mongoHost = properties.getProperty("mongo.host");
        this.mongoPort = Integer.parseInt(properties.getProperty("mongo.port"));
        this.esClusterName = properties.getProperty("es.cluster.name");
        this.esHost = properties.getProperty("es.host");
        this.esPort = Integer.parseInt(properties.getProperty("es.port"));
    }

    /** 用于将 jedis 注册为一个 Bean */
    @Bean("jedis")
    public Jedis getJedis() {
        Jedis jedis = new Jedis(this.esHost);
        return jedis;
    }

    /** 用户将 mongoClient 注册为一个 Bean */
    @Bean("mongoClient")
    public MongoClient getMongoClient() {
        MongoClient mongoClient = new MongoClient(this.mongoHost, this.mongoPort);
        return mongoClient;
    }

    /** 用户将 ES 注册为一个 Bean */
    @Bean("transportClient")
    public TransportClient getTransportClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", this.esClusterName).build();
        PreBuiltTransportClient esClient = new PreBuiltTransportClient(settings);
        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.esHost), this.esPort));
        return esClient;
    }



}
