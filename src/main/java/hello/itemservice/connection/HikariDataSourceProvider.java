package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static hello.itemservice.connection.ConnectionConst.PASSWORD;
import static hello.itemservice.connection.ConnectionConst.URL;
import static hello.itemservice.connection.ConnectionConst.USERNAME;
import lombok.extern.slf4j.Slf4j;

/**
 * HikariCP 연결 풀을 사용하여 데이터베이스 연결을 제공하는 구현체
 * 고성능 연결 풀링과 자동 리소스 관리를 제공
 */
@Slf4j
public class HikariDataSourceProvider implements DataSourceProvider {
    
    private final HikariDataSource dataSource;
    
    public HikariDataSourceProvider() {
        this.dataSource = createHikariDataSource();
        log.info("HikariCP 데이터소스 초기화 완료");
    }
    
    private HikariDataSource createHikariDataSource() {
        HikariConfig config = new HikariConfig();
        
        // 기본 연결 설정
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        
        // 연결 풀 설정
        config.setMaximumPoolSize(10);           // 최대 연결 수
        config.setMinimumIdle(5);                // 최소 유휴 연결 수
        config.setConnectionTimeout(30000);      // 연결 타임아웃 (30초)
        config.setIdleTimeout(600000);           // 유휴 타임아웃 (10분)
        config.setMaxLifetime(1800000);          // 최대 생명주기 (30분)
        
        // 연결 유효성 검사
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);       // 검증 타임아웃 (5초)
        
        // 성능 최적화
        config.setLeakDetectionThreshold(60000); // 연결 누수 감지 (1분)
        config.setPoolName("ItemServicePool");    // 풀 이름
        
        // H2 데이터베이스 특화 설정
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return new HikariDataSource(config);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            log.debug("HikariCP 연결 획득: {}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            log.error("HikariCP 연결 획득 실패", e);
            throw new IllegalStateException("HikariCP를 통한 데이터베이스 연결에 실패했습니다.", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "HikariCP";
    }
    
    @Override
    public boolean isConnectionValid() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        
        try (Connection connection = dataSource.getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.warn("연결 유효성 검사 실패", e);
            return false;
        }
    }
    
    /**
     * 데이터소스 통계 정보를 반환합니다.
     * @return 연결 풀 상태 정보
     */
    public String getPoolStats() {
        if (dataSource == null || dataSource.isClosed()) {
            return "데이터소스가 초기화되지 않았거나 종료되었습니다.";
        }
        
        return String.format(
            "HikariCP 풀 상태 - 활성: %d, 유휴: %d, 대기: %d, 총 연결: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
            dataSource.getHikariPoolMXBean().getTotalConnections()
        );
    }
    
    /**
     * 리소스를 안전하게 해제합니다.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("HikariCP 데이터소스 종료 중...");
            dataSource.close();
            log.info("HikariCP 데이터소스 종료 완료");
        }
    }
}
