package com.goal.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliPayConfigProperties.class)
public class AliPayConfig {

    @Bean(name = "alipayClient")
    @ConditionalOnProperty(value = "alipay.config.enabled", havingValue = "true", matchIfMissing = false)
    public AlipayClient alipayClient(AliPayConfigProperties properties) {

        return new DefaultAlipayClient(
                properties.getServerUrl(),
                properties.getAppid(),
                properties.getPrivateKey(),
                properties.getFormat(),
                properties.getCharset(),
                properties.getAlipayPublicKey(),
                properties.getSignType()
        );

    }

}
