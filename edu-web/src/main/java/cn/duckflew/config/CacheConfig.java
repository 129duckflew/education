package cn.duckflew.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    //缓存管理器
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory).build();
    }
    //自定义缓存key生成策略
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuffer sb = new StringBuffer();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (int i = 0; i < params.length; i++) {
                if (i==0) {
                    sb.append(params[i].toString());
                } else {
                    sb.append(",").append(params[i].toString());
                }
            }
            System.out.println("调用Redis生成key："+ sb);
            return sb.toString();
        };
    }
}
