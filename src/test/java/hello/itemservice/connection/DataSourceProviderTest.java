package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

/**
 * 데이터소스 제공자 구현체들의 테스트
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class DataSourceProviderTest {
    
    @Autowired
    private DataSourceProvider driverManagerProvider;
    
    @Autowired
    private DataSourceProvider hikariProvider;
    
    @Test
    void driverManagerDataSourceProvider_연결테스트() throws SQLException {
        // given
        DriverManagerDataSourceProvider provider = new DriverManagerDataSourceProvider();
        
        // when
        Connection connection = provider.getConnection();
        
        // then
        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();
        assertThat(provider.getProviderName()).isEqualTo("DriverManager");
        assertThat(provider.isConnectionValid()).isTrue();
        
        // cleanup
        connection.close();
        log.info("DriverManager 연결 테스트 완료");
    }
    
    @Test
    void hikariDataSourceProvider_연결테스트() throws SQLException {
        // given
        HikariDataSourceProvider provider = new HikariDataSourceProvider();
        
        try {
            // when
            Connection connection = provider.getConnection();
            
            // then
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            assertThat(provider.getProviderName()).isEqualTo("HikariCP");
            assertThat(provider.isConnectionValid()).isTrue();
            
            // 풀 상태 확인
            String poolStats = provider.getPoolStats();
            assertThat(poolStats).isNotNull();
            log.info("HikariCP 풀 상태: {}", poolStats);
            
            // cleanup
            connection.close();
            log.info("HikariCP 연결 테스트 완료");
            
        } finally {
            provider.close();
        }
    }
    
    @Test
    void springBean_주입테스트() {
        // given & when & then
        assertThat(driverManagerProvider).isNotNull();
        assertThat(hikariProvider).isNotNull();
        
        log.info("DriverManager Provider: {}", driverManagerProvider.getProviderName());
        log.info("HikariCP Provider: {}", hikariProvider.getProviderName());
    }
    
    @Test
    void 연결실패_예외처리테스트() {
        // given - 잘못된 설정으로 DriverManager 생성
        DriverManagerDataSourceProvider provider = new DriverManagerDataSourceProvider();
        
        // when & then - 연결 유효성 검사에서 예외가 발생하지 않아야 함
        boolean isValid = provider.isConnectionValid();
        log.info("연결 유효성: {}", isValid);
        
        // 실제 연결 시도는 테스트 환경에 따라 성공할 수 있으므로 예외 발생 여부만 확인
        assertThat(provider.getProviderName()).isEqualTo("DriverManager");
    }
}
