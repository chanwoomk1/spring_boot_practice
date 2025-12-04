package hello.itemservice.service.item;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    /**
     * 아이템 저장
     */
    public Item saveItem(Item item){
        return itemRepository.save(item);
    }

    /**
     * ID로 아이템 조회
     */
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
            () -> new NoSuchElementException("상품 ID를 찾을 수 없습니다: " + itemId));
    }

    /**
     * 모든 아이템 조회
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 아이템 수정
     */
    public void updateItem(Long itemId, Item updateParam) {
        itemRepository.update(itemId, updateParam);
    }
}
