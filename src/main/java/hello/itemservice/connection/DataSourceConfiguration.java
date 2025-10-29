package hello.itemservice.connection;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static hello.itemservice.connection.ConnectionConst.PASSWORD;
import static hello.itemservice.connection.ConnectionConst.URL;
import static hello.itemservice.connection.ConnectionConst.USERNAME;

/**
 * 데이터소스 제공자 설정을 위한 Configuration 클래스
 * 다양한 데이터소스 구현체를 Spring Bean으로 등록
 */
@Configuration
public class DataSourceConfiguration {
    
    /**
     * HikariCP DataSource - 트랜잭션 지원을 위한 메인 DataSource
     * ItemDBRepository가 사용하는 DataSource
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // 기본 연결 설정
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        
        // 연결 풀 설정
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        // 연결 유효성 검사
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // 성능 최적화
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("ItemServicePool");
        
        // H2 데이터베이스 특화 설정
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return new HikariDataSource(config);
    }
}
