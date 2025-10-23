package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * HikariDataSourceProvider의 리소스 관리 테스트
 */
@Slf4j
class HikariDataSourceProviderResourceTest {
    
    @Test
    void 정상적인_리소스_생명주기_테스트() throws SQLException {
        // given
        try (HikariDataSourceProvider provider = new HikariDataSourceProvider()) {
            // when - 초기화 확인
            assertThat(provider.isClosed()).isFalse();
            assertThat(provider.getStatus()).isEqualTo("정상 동작 중");
            assertThat(provider.getProviderName()).isEqualTo("HikariCP");
            
            // 연결 획득 테스트
            try (Connection connection = provider.getConnection()) {
                assertThat(connection).isNotNull();
                assertThat(connection.isClosed()).isFalse();
                log.info("연결 획득 성공: {}", connection.getClass().getSimpleName());
            }
            
            // 풀 상태 확인
            String poolStats = provider.getPoolStats();
            assertThat(poolStats).isNotNull();
            log.info("풀 상태: {}", poolStats);
        }
        
        // then - try-with-resources 블록을 벗어나면 자동으로 close() 호출됨
        // 하지만 provider 변수에 접근할 수 없으므로 별도의 테스트로 분리
    }
    
    @Test
    void 종료_후_상태_확인_테스트() {
        // given
        // 주의: 종료 후 상태 확인을 위해 try-with-resources 사용 불가
        HikariDataSourceProvider provider = new HikariDataSourceProvider();
        
        try {
            // when - 리소스 해제
            provider.close();
            
            // then - 종료 확인
            assertThat(provider.isClosed()).isTrue();
            assertThat(provider.getStatus()).isEqualTo("종료됨");
            
            // 종료 후 연결 시도 시 예외 발생 확인
            assertThatThrownBy(() -> provider.getConnection())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("초기화되지 않았거나 종료되었습니다");
        } finally {
            // 이미 종료된 상태이므로 추가 close() 호출은 안전함
            provider.close();
        }
    }
    
    @Test
    void try_with_resources_패턴_테스트() {
        // given & when & then
        try (HikariDataSourceProvider provider = new HikariDataSourceProvider()) {
            assertThat(provider.isClosed()).isFalse();
            
            // 연결 테스트
            try (Connection connection = provider.getConnection()) {
                assertThat(connection).isNotNull();
            }
            
        } catch (SQLException e) {
            log.error("테스트 중 SQL 예외 발생", e);
        }
        
        // try-with-resources 블록을 벗어나면 자동으로 close() 호출됨
        log.info("try-with-resources 패턴 테스트 완료");
    }
    
    @Test
    void 중복_종료_호출_테스트() {
        // given
        HikariDataSourceProvider provider = new HikariDataSourceProvider();
        
        try {
            // when - 첫 번째 종료
            provider.close();
            assertThat(provider.isClosed()).isTrue();
            
            // when - 두 번째 종료 (중복 호출)
            provider.close(); // 예외가 발생하지 않아야 함
            
            // then
            assertThat(provider.isClosed()).isTrue();
            log.info("중복 종료 호출 테스트 완료 - 예외 없이 처리됨");
            
        } catch (Exception e) {
            log.error("중복 종료 호출 중 예외 발생", e);
            throw e;
        }
    }
    
    @Test
    void 풀_상태_모니터링_테스트() throws SQLException {
        // given
        try (HikariDataSourceProvider provider = new HikariDataSourceProvider()) {
            
            // when - 여러 연결을 동시에 생성
            try (Connection conn1 = provider.getConnection();
                 Connection conn2 = provider.getConnection();
                 Connection conn3 = provider.getConnection()) {
                
                // then - 풀 상태 확인 (연결들이 활성 상태에 있는지 확인)
                String poolStats = provider.getPoolStats();
                assertThat(poolStats).isNotNull();
                assertThat(poolStats).contains("활성");
                assertThat(poolStats).contains("유휴");
                assertThat(poolStats).contains("총 연결");
                
                log.info("다중 연결 상태: {}", poolStats);
                
                // 연결들이 실제로 사용 가능한지 확인
                assertThat(conn1.isClosed()).isFalse();
                assertThat(conn2.isClosed()).isFalse();
                assertThat(conn3.isClosed()).isFalse();
            }
            
            // 연결 반환 후 상태 확인
            String finalStats = provider.getPoolStats();
            log.info("연결 반환 후 상태: {}", finalStats);
        }
    }
}
