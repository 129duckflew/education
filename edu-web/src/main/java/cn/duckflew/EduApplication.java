package cn.duckflew;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@SpringBootApplication
@MapperScan("cn.duckflew.mapper")
@EnableCaching
public class EduApplication
{

    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class,args);
    }
}
