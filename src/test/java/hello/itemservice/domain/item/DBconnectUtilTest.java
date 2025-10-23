package hello.itemservice.domain.item;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import hello.itemservice.connection.DriverManagerDataSourceProvider;

/**
 * 데이터베이스 연결 테스트
 * DriverManagerDataSourceProvider를 사용한 연결 테스트
 */
public class DBconnectUtilTest {
    
    @Test
    void connection() throws SQLException {
        // given
        DriverManagerDataSourceProvider provider = new DriverManagerDataSourceProvider();
        
        // when & then
        try (Connection connection = provider.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            assertThat(provider.getProviderName()).isEqualTo("DriverManager");
        }
    }
}
