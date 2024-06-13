package com.goal.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class ThreadPoolConfig {

//    @Bean
//    @ConditionalOnMissingBean(ThreadPoolExecutor.class)
//    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        // 实例化策略
//        RejectedExecutionHandler handler;
//        switch (properties.getPolicy()) {
//            case "DiscardPolicy":
//                handler = new ThreadPoolExecutor.DiscardPolicy();
//                break;
//            case "DiscardOldestPolicy":
//                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
//                break;
//            case "CallerRunsPolicy":
//                handler = new ThreadPoolExecutor.CallerRunsPolicy();
//                break;
//            case "AbortPolicy":
//            default:
//                handler = new ThreadPoolExecutor.AbortPolicy();
//                break;
//        }
//        // 创建线程池
//        return new ThreadPoolExecutor(properties.getCorePoolSize(),
//                properties.getMaxPoolSize(),
//                properties.getKeepAliveTime(),
//                TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(properties.getBlockQueueSize()),
//                Executors.defaultThreadFactory(),
//                handler);
//    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(ThreadPoolConfigProperties properties) {

        RejectedExecutionHandler handler;
        switch (properties.getPolicy()) {
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "AbortPolicy":
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数目：I/O密集型
        taskExecutor.setCorePoolSize(properties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(properties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(properties.getBlockQueueSize());
        taskExecutor.setKeepAliveSeconds(properties.getKeepAliveTime());

        taskExecutor.setRejectedExecutionHandler(handler);

        return taskExecutor;
    }


}
