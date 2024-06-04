package com.goal.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPwd;

    /**
     * 配置分布式锁
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        // 单节点
        config.useSingleServer()
                .setPassword(redisPwd)
                .setAddress("redis://" + redisHost + ":" + redisPort);

        // 多节点
//        config.useClusterServers()
//                .addNodeAddress("redis://192.168.1.1:6379", "redis://192.168.1.2:6379");

        return Redisson.create(config);
    }

}
