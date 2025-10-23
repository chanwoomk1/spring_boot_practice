package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.itemservice.connection.ConnectionConst.PASSWORD;
import static hello.itemservice.connection.ConnectionConst.URL;
import static hello.itemservice.connection.ConnectionConst.USERNAME;
import lombok.extern.slf4j.Slf4j;

/**
 * DriverManager를 사용하여 데이터베이스 연결을 제공하는 구현체
 * DataSourceProvider 인터페이스의 구현체로 DriverManager 기반 연결 제공
 */
@Slf4j
public class DriverManagerDataSourceProvider implements DataSourceProvider {
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("DriverManager 연결 생성: {}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            log.error("DriverManager 연결 실패: URL={}, USERNAME={}", URL, USERNAME, e);
            throw new IllegalStateException("데이터베이스 연결에 실패했습니다.", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "DriverManager";
    }
    
    @Override
    public boolean isConnectionValid() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.warn("연결 유효성 검사 실패", e);
            return false;
        }
    }
}
