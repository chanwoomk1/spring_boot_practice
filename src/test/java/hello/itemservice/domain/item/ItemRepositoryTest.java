package hello.itemservice.domain.item;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat; // Assertions 추가
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ItemRepositoryTest {
    
    // 클래스 이름이 ItemDBRepository라고 가정
    ItemDBRepository itemDBRepository = new ItemDBRepository();
    
    // @AfterEach
    // void tearDown() throws SQLException {
    //     itemDBRepository.clearTable();
    // }

    @Test
    void crud() throws SQLException {
        Item item = new Item("테스트Item_A", 15000, 5);
        Item savedItem = itemDBRepository.save(item);
        assertThat(savedItem).isNotNull();
        assertThat(savedItem).isEqualTo(item);

        Item findItem = itemDBRepository.findById(savedItem.getId());
        assertThat(findItem).isNotNull();
        assertThat(findItem).isEqualTo(savedItem);
        
        log.info("Original Item (Saved): {}", savedItem);
        log.info("Found Item (Queried):   {}", findItem);
        
    }
    
    @Test
    void findByNonExistingId() throws SQLException {
        // 존재하지 않는 ID(예: 9999)를 조회하면 예외가 발생해야 함을 검증
        assertThatThrownBy(() -> itemDBRepository.findById(9999L))
            .isInstanceOf(NoSuchElementException.class);
    }
}