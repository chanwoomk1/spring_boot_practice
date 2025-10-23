package hello.itemservice.web.item.basic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.connection.DataSourceProvider;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;



@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;
    private final DataSourceProvider dataSourceProvider;

    @GetMapping
    public String getMethodName(Model model) {
        List<Item> items=itemRepository.findAll();
        model.addAttribute("items",items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId,Model model) {
        Item item=itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addItemV3(Model model) {
        return "basic/addForm";
    }
    @PostMapping("/add")
    public String addItemV3(Item item,RedirectAttributes redirectAttributes) {
        Item savedItem=itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        redirectAttributes.addAttribute("status",true);
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Model model) {
        Item item=itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return "basic/editForm";
    }
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Item item) {
        itemRepository.update(itemId,item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 현재 사용 중인 데이터소스 정보를 확인하는 엔드포인트
     */
    @GetMapping("/datasource-info")
    public String getDataSourceInfo(Model model) {
        model.addAttribute("providerName", dataSourceProvider.getProviderName());
        model.addAttribute("isConnectionValid", dataSourceProvider.isConnectionValid());
        
        // HikariCP인 경우 추가 정보 제공
        if (dataSourceProvider instanceof hello.itemservice.connection.HikariDataSourceProvider hikariProvider) {
            model.addAttribute("poolStats", hikariProvider.getPoolStats());
        }
        
        return "basic/datasource-info";
    }

    // @PostConstruct
    // public void init(){
    //     itemRepository.save(new Item("testA",1000,10));
    //     itemRepository.save(new Item("testB",2000,10));
    // }
    
}
