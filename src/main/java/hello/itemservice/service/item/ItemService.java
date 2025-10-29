package hello.itemservice.service.item;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final PlatformTransactionManager transactionManager;
    private final ItemRepository itemRepository;

    /**
     * 아이템 저장
     * 트랜잭션 관리
     */
    public Item saveItem(Item item){
        TransactionStatus status=transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Item savedItem=itemRepository.save(item);
            transactionManager.commit(status);
            return savedItem;
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    /**
     * ID로 아이템 조회
     */
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    /**
     * 모든 아이템 조회
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 아이템 수정
     * 트랜잭션 관리
     */
    public void updateItem(Long itemId, Item updateParam) {
        TransactionStatus status=transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            itemRepository.update(itemId, updateParam);
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }
}
