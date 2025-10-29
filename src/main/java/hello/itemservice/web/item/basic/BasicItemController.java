package hello.itemservice.web.item.basic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.domain.item.Item;
import hello.itemservice.service.item.ItemService;
import lombok.RequiredArgsConstructor;



@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemService itemService;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        return "basic/addForm";
    }

    @PostMapping("/add")
    public String addItem(Item item, RedirectAttributes redirectAttributes) {
        try {
            Item savedItem = itemService.saveItem(item);
            redirectAttributes.addAttribute("itemId", savedItem.getId());
            redirectAttributes.addAttribute("status", true);
            return "redirect:/basic/items/{itemId}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "아이템 저장에 실패했습니다.");
            return "redirect:/basic/items/add";
        }
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Item item) {
        try {
            itemService.updateItem(itemId, item);
            return "redirect:/basic/items/{itemId}";
        } catch (Exception e) {
            return "redirect:/basic/items/{itemId}/edit";
        }
    }

    // @PostConstruct
    // public void init(){
    //     itemService.saveItem(new Item("testA",1000,10));
    //     itemService.saveItem(new Item("testB",2000,10));
    // }
    
}
