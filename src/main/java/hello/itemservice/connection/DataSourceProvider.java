package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 제공하는 인터페이스
 * 다양한 데이터소스 구현체를 지원할 수 있도록 설계
 */
public interface DataSourceProvider {
    
    /**
     * 데이터베이스 연결을 반환합니다.
     * @return 데이터베이스 연결 객체
     * @throws SQLException 데이터베이스 연결 실패 시
     */
    Connection getConnection() throws SQLException;
    
    /**
     * 데이터소스 제공자의 이름을 반환합니다.
     * @return 데이터소스 제공자 이름
     */
    String getProviderName();
    
    /**
     * 연결이 유효한지 확인합니다.
     * @return 연결 유효성 여부
     */
    boolean isConnectionValid();
}
