package hello.itemservice.domain.item;
import java.util.List;
import java.util.Optional;

public interface  ItemRepository {


    public Item save(Item item);

    public Optional<Item> findById(Long id);
    public List<Item>findAll();
    public void update(Long itemId,Item updateParam);
    public void clearStore();

}
