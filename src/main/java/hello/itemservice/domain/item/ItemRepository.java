package hello.itemservice.domain.item;
import java.util.List;

public interface  ItemRepository {


    public Item save(Item item);

    public Item findById(Long id);
    public List<Item>findAll();
    public void update(Long itemId,Item updateParam);
    public void clearStore();

}
