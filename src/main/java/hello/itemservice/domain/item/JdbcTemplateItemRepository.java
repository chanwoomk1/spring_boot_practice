package hello.itemservice.domain.item;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository; // Optional 반환을 위해 사용

@Repository
public class JdbcTemplateItemRepository implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcTemplateItemRepository(DataSource dataSource) {
        // DataSource만 주입받으면 JdbcTemplate이 자동으로 생성됨
        this.template = new JdbcTemplate(dataSource); 
    }

    // ResultSet의 결과를 Item 객체로 매핑하는 RowMapper 정의
    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> {
            Item item = new Item(
                rs.getString("item_name"),
                rs.getInt("price"),
                rs.getInt("quantity")
            );
            item.setId(rs.getLong("id"));
            return item;
        };
    }

    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO item (item_name, price, quantity) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 생성된 키를 담을 객체

        template.update(connection -> {
            // PreparedStatement를 직접 생성하고 RETURN_GENERATED_KEYS 옵션을 지정
            PreparedStatement ps = connection.prepareStatement(sql, 
                new String[]{"id"}); // ID 컬럼 이름을 지정하거나 Statement.RETURN_GENERATED_KEYS 사용
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("데이터베이스에서 생성된 ID를 획득하지 못했습니다.");
        }
        
        item.setId(key.longValue());
        return item;
    }

    @Override
    // ID가 없을 경우를 대비해 Optional을 반환하도록 수정 (권장 방식)
    public Optional<Item> findById(Long id) { 
        String sql = "SELECT id, item_name, price, quantity FROM item WHERE id = ?";
        try {
            // queryForObject는 결과가 1개일 때 사용. 없으면 EmptyResultDataAccessException 발생
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // 찾는 데이터가 없을 경우 예외를 잡고 빈 Optional 반환
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT id, item_name, price, quantity FROM item";
        // query는 결과가 List일 때 사용
        return template.query(sql, itemRowMapper()); 
    }

    @Override
    public void update(Long itemId, Item updateParam) {
        String sql = "UPDATE item SET item_name=?, price=?, quantity=? WHERE id=?";
        template.update(sql, 
            updateParam.getItemName(), 
            updateParam.getPrice(), 
            updateParam.getQuantity(), 
            itemId
        );
    }
    
    @Override
    public void clearStore() {
        String sql = "TRUNCATE TABLE item";
        template.update(sql);
    }
}