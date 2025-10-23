package hello.itemservice.domain.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.springframework.stereotype.Repository;

import hello.itemservice.connection.DataSourceProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ItemDBRepository implements ItemRepository {

    private final DataSourceProvider dataSourceProvider;
    
    public ItemDBRepository(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    /**
     * 데이터베이스 작업을 실행하는 공통 메서드
     * @param sql 실행할 SQL 쿼리
     * @param params PreparedStatement에 설정할 파라미터들
     * @param resultSetHandler ResultSet을 처리하는 함수
     * @return 처리 결과
     */
    private <T> T executeQuery(String sql, Object[] params, Function<ResultSet, T> resultSetHandler) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = dataSourceProvider.getConnection();
            pstmt = con.prepareStatement(sql);
            
            // 파라미터 설정
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            
            rs = pstmt.executeQuery();
            return resultSetHandler.apply(rs);
            
        } catch (SQLException e) {
            log.error("DB 쿼리 실행 오류: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 데이터베이스 업데이트 작업을 실행하는 공통 메서드
     * @param sql 실행할 SQL 쿼리
     * @param params PreparedStatement에 설정할 파라미터들
     * @return 영향받은 행의 수
     */
    private int executeUpdate(String sql, Object[] params) {
        Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
            con = dataSourceProvider.getConnection();
            pstmt = con.prepareStatement(sql);
            
            // 파라미터 설정
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("DB 업데이트 실행 오류: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * Generated Key를 포함한 INSERT 작업을 실행하는 공통 메서드
     * @param sql 실행할 SQL 쿼리
     * @param params PreparedStatement에 설정할 파라미터들
     * @param keyHandler Generated Key를 처리하는 함수
     * @return 처리 결과
     */
    private <T> T executeInsert(String sql, Object[] params, Function<ResultSet, T> keyHandler) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = dataSourceProvider.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 파라미터 설정
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            return keyHandler.apply(rs);
            
        } catch (SQLException e) {
            log.error("DB INSERT 실행 오류: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO item (item_name, price, quantity) VALUES (?, ?, ?)";
        Object[] params = {item.getItemName(), item.getPrice(), item.getQuantity()};
        
        return executeInsert(sql, params, rs -> {
            try {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                    return item;
                } else {
                    throw new NoSuchElementException("ID를 생성하지 못했습니다.");
                }
            } catch (SQLException e) {
                log.error("Generated Key 처리 오류", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Item findById(Long id) {
        String sql = "SELECT id, item_name, price, quantity FROM item WHERE id = ?";
        Object[] params = {id};
        
        return executeQuery(sql, params, rs -> {
            try {
                if (rs.next()) {
                    Item item = new Item(
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("quantity")
                    );
                    item.setId(rs.getLong("id"));
                    return item;
                } else {
                    throw new NoSuchElementException("ID에 해당하는 상품을 찾을 수 없습니다. id = " + id);
                }
            } catch (SQLException e) {
                log.error("ResultSet 처리 오류", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT id, item_name, price, quantity FROM item";
        
        return executeQuery(sql, null, rs -> {
            List<Item> items = new ArrayList<>();
            try {
                while (rs.next()) {
                    Item item = new Item(
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("quantity")
                    );
                    item.setId(rs.getLong("id"));
                    items.add(item);
                }
                return items;
            } catch (SQLException e) {
                log.error("ResultSet 처리 오류", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void update(Long itemId, Item updateParam) {
        String sql = "UPDATE item SET item_name=?, price=?, quantity=? WHERE id=?";
        Object[] params = {updateParam.getItemName(), updateParam.getPrice(), updateParam.getQuantity(), itemId};
        
        int resultSize = executeUpdate(sql, params);
        log.info("update resultSize={}", resultSize);
    }
    
    // 이 메서드는 ItemRepository 인터페이스에 없으므로 그대로 둡니다.
    public void delete(Long itemId) {
        String sql = "DELETE FROM item WHERE id=?";
        Object[] params = {itemId};
        
        int resultSize = executeUpdate(sql, params);
        log.info("delete resultSize={}", resultSize);
    }

    @Override
    public void clearStore() {
        clearTable();
    }

    public void clearTable() {
        String sql = "TRUNCATE TABLE item";
        executeUpdate(sql, null);
    }

    /**
     * 데이터베이스 리소스를 안전하게 해제하는 메서드
     * 리소스 해제 순서: ResultSet -> Statement -> Connection
     */
    private void close(Connection con, Statement st, ResultSet rs) {
        closeResource(rs, "ResultSet");
        closeResource(st, "Statement");
        closeResource(con, "Connection");
    }

    /**
     * 개별 리소스를 안전하게 해제하는 헬퍼 메서드
     */
    private void closeResource(AutoCloseable resource, String resourceName) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                log.warn("{} 닫기 오류", resourceName, e);
            }
        }
    }
}