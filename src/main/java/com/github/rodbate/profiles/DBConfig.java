package com.github.rodbate.profiles;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@PropertySource("app.properties")
public class DBConfig {


    @Bean("dataSource")
    @Profile("dev")
    @Annot(a = {"a1", "a2"}, b = {1, 2}, c = false)
    //@Conditional(ScalaCondition.class)
    public DataSource dataSourceDev(@Value("${password}") String password){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://192.168.76.146:3306/test?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&useSSL=false");
        ds.setUsername("rodbate1");
        ds.setPassword(password);
        ds.setMaxActive(10);
        ds.setInitialSize(5);
        ds.setMinIdle(1);
        ds.setMaxWait(1000);
        return ds;
    }

    /*@Bean
    @Autowired
    public DataSourceTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }*/

    @Bean("dataSource")
    @Profile("prod")
    public DataSource dataSourceProd(){
        return null;
    }


    /*@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }*/


}
