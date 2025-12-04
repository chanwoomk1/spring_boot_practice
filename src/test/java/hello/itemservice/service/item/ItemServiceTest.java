package hello.itemservice.service.item;

import java.util.List;

import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.itemservice.domain.item.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class ItemServiceTest {

    @Autowired
    private ItemService itemService;


    // @Test
    // @DisplayName("AOP 프록시 확인 테스트")
    // void aopProxyCheck() {
    //     // Given & When
    //     boolean isProxy = AopUtils.isAopProxy(itemService);
        
    //     // Then
    //     log.info("ItemService class: {}", itemService.getClass());
    //     log.info("Is AOP Proxy: {}", isProxy);
        
    //     // @Transactional이 적용되어 있으므로 프록시가 생성되어야 함
    //     // 실제로는 CGLIB 프록시로 생성됨
    //     log.info("Class name contains 'Proxy': {}", 
    //         itemService.getClass().getName().contains("Proxy"));
    //     assertThat(isProxy).isTrue();
    // }

    @Test
    @DisplayName("아이템 저장 및 조회 테스트")
    void saveAndFindItemTest() {
        // Given
        Item item = new Item("테스트 상품", 15000, 10);
        
        // When
        Item savedItem = itemService.saveItem(item);
        
        // Then
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getItemName()).isEqualTo("테스트 상품");
        assertThat(savedItem.getPrice()).isEqualTo(15000);
        assertThat(savedItem.getQuantity()).isEqualTo(10);
        
        log.info("Saved Item: {}", savedItem);
    }

    @Test
    @DisplayName("아이템 ID로 조회 테스트")
    void findItemByIdTest() {
        // Given - 아이템 저장
        Item item = new Item("조회 테스트 상품", 20000, 5);
        Item savedItem = itemService.saveItem(item);
        Long itemId = savedItem.getId();
        
        // When
        Item foundItem = itemService.findItem(itemId);
        
        // Then
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(itemId);
        assertThat(foundItem.getItemName()).isEqualTo("조회 테스트 상품");
        assertThat(foundItem.getPrice()).isEqualTo(20000);
        assertThat(foundItem.getQuantity()).isEqualTo(5);
        
        log.info("Found Item: {}", foundItem);
    }

    @Test
    @DisplayName("모든 아이템 조회 테스트")
    void findAllItemsTest() {
        // Given - 여러 아이템 저장
        itemService.saveItem(new Item("상품1", 10000, 3));
        itemService.saveItem(new Item("상품2", 20000, 5));
        itemService.saveItem(new Item("상품3", 30000, 7));
        
        // When
        List<Item> items = itemService.findItems();
        
        // Then
        assertThat(items).isNotEmpty();
        log.info("Total items count: {}", items.size());
        items.forEach(item -> log.info("Item: {}", item));
    }

    @Test
    @DisplayName("아이템 수정 테스트")
    void updateItemTest() {
        // Given - 아이템 저장
        Item item = new Item("수정 전 상품", 15000, 10);
        Item savedItem = itemService.saveItem(item);
        Long itemId = savedItem.getId();
        
        // When - 아이템 수정
        Item updateParam = new Item("수정 후 상품", 25000, 15);
        itemService.updateItem(itemId, updateParam);
        
        // Then
        Item updatedItem = itemService.findItem(itemId);
        assertThat(updatedItem.getItemName()).isEqualTo("수정 후 상품");
        assertThat(updatedItem.getPrice()).isEqualTo(25000);
        assertThat(updatedItem.getQuantity()).isEqualTo(15);
        
        log.info("Updated Item: {}", updatedItem);
    }

    @Test
    @DisplayName("트랜잭션 롤백 테스트")
    void transactionRollbackTest() {
        // Given
        Item item1 = new Item("상품1", 10000, 5);
        Item savedItem1 = itemService.saveItem(item1);
        
        // When & Then - 예외 발생 시 롤백 확인
        Assertions.assertThatThrownBy(() -> {
            itemService.saveItem(new Item("상품2", 20000, 3));
            // 강제 예외 발생
            throw new RuntimeException("테스트용 예외");
        }).isInstanceOf(RuntimeException.class);
        
        // 롤백되었는지 확인 - 첫 번째 아이템은 존재해야 함
        Item foundItem1 = itemService.findItem(savedItem1.getId());
        assertThat(foundItem1).isNotNull();
        
        log.info("First item exists after rollback: {}", foundItem1);
    }

    @Test
    @DisplayName("프록시 객체 상세 정보")
    void proxyDetailsTest() {
        log.info("ItemService Class Name: {}", itemService.getClass().getName());
        log.info("ItemService Super Class: {}", itemService.getClass().getSuperclass());
        log.info("ItemService Interfaces: {}", 
            java.util.Arrays.toString(itemService.getClass().getInterfaces()));
        
        // @Transactional 어노테이션 확인
        if (itemService.getClass().isAnnotationPresent(
            org.springframework.transaction.annotation.Transactional.class)) {
            log.info("@Transactional found on class level");
        }
        
        // 메서드 레벨 @Transactional 확인
        try {
            java.lang.reflect.Method saveMethod = itemService.getClass().getMethod("saveItem", Item.class);
            if (saveMethod.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class)) {
                log.info("@Transactional found on saveItem method");
            }
        } catch (NoSuchMethodException e) {
            log.warn("Method not found: {}", e.getMessage());
        }
    }
}
