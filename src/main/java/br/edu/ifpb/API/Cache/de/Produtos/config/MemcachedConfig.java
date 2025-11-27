package br.edu.ifpb.API.Cache.de.Produtos.config;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MemcachedConfig {

    @Value("${memcached.servers}")
    private String servers;

    @Value("${memcached.pool-size:10}")
    private int poolSize;

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers));
        builder.setConnectionPoolSize(poolSize);
        return builder.build();
    }
}