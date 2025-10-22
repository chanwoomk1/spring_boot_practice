package hello.itemservice.domain.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import hello.itemservice.connection.DBconnectionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ItemDBRepository implements ItemRepository {

    @Override
    public Item save(Item item) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO item (item_name, price, quantity) VALUES (?, ? ,?)";
        try {
            con = DBconnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, item.getItemName());
            pstmt.setInt(2, item.getPrice());
            pstmt.setInt(3, item.getQuantity());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getLong(1));
            } else {
                throw new NoSuchElementException("ID를 생성하지 못했습니다.");
            }
            return item;
        } catch (SQLException e) {
            log.error("DB 저장 오류", e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public Item findById(Long id) {
        String sql = "SELECT id, item_name, price, quantity FROM item WHERE id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBconnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Item item = new Item(rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("quantity"));
                item.setId(rs.getLong("id"));
                return item;
            } else {
                throw new NoSuchElementException("ID에 해당하는 상품을 찾을 수 없습니다. id = " + id);
            }
        } catch (SQLException e) {
            log.error("DB 조회 오류", e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT id, item_name, price, quantity FROM item";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Item> items = new ArrayList<>();

        try {
            con = DBconnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

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
            log.error("DB 전체 조회 오류", e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public void update(Long itemId, Item updateParam) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE item SET item_name=?, price=?, quantity=? WHERE id=?";

        try {
            con = DBconnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, updateParam.getItemName());
            pstmt.setInt(2, updateParam.getPrice());
            pstmt.setInt(3, updateParam.getQuantity());
            pstmt.setLong(4, itemId);

            int resultSize = pstmt.executeUpdate();
            log.info("update resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("DB 수정 오류", e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, null);
        }
    }
    
    // 이 메서드는 ItemRepository 인터페이스에 없으므로 그대로 둡니다.
    public void delete(Long itemId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM item WHERE id=?";

        try {
            con = DBconnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, itemId);

            int resultSize = pstmt.executeUpdate();
            log.info("delete resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("DB 삭제 오류", e);
            throw new RuntimeException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void clearStore() {
        clearTable();
    }

    public void clearTable() {
        try (Connection con = DBconnectionUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE item");
        } catch (SQLException e) {
            log.error("테이블 정리 오류", e);
            throw new RuntimeException(e);
        }
    }

    private void close(Connection con, Statement st, ResultSet rs) {
        // 리소스 해제 순서: ResultSet -> Statement -> Connection
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("ResultSet 닫기 오류", e);
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                log.info("Statement 닫기 오류", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("Connection 닫기 오류", e);
            }
        }
    }
}