package hello.itemservice.connection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 데이터소스 제공자 설정을 위한 Configuration 클래스
 * 다양한 데이터소스 구현체를 Spring Bean으로 등록
 */
@Configuration
public class DataSourceConfiguration {
    
    /**
     * DriverManager 기반 데이터소스 제공자
     * 기본적으로 사용되는 구현체
     */
    @Bean
    @Primary
    public DataSourceProvider driverManagerDataSourceProvider() {
        return new DriverManagerDataSourceProvider();
    }
    
    /**
     * HikariCP 기반 데이터소스 제공자
     * 고성능 연결 풀링이 필요한 경우 사용
     */
    @Bean
    public DataSourceProvider hikariDataSourceProvider() {
        return new HikariDataSourceProvider();
    }
}
