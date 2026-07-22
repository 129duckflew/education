package cn.duckflew;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@MapperScan("cn.duckflew.mapper")
@SpringBootApplication
public class AdminWebApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AdminWebApplication.class,args);
    }
}
