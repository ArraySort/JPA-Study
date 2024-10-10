package jpabook.jpashop.controller;

import jpabook.jpashop.Entity.item.Book;
import jpabook.jpashop.Entity.item.Item;
import jpabook.jpashop.dto.request.AddItemReqDTO;
import jpabook.jpashop.dto.response.EditItemReqDTO;
import jpabook.jpashop.dto.response.EditItemResDTO;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	// 상품 등록 페이지
	@GetMapping("/items/new")
	public String showAddItemPage(Model model) {
		model.addAttribute("form", new AddItemReqDTO());
		return "items/createItemForm";
	}

	// 상품 등록 요청
	@PostMapping("/items/new")
	public String create(AddItemReqDTO dto) {
		Book book = Book.createOf(dto);
		itemService.saveItem(book);

		return "redirect:/";
	}

	// 상품 리스트 조회 페이지
	@GetMapping("/items")
	public String showItemListPage(Model model) {
		List<Item> items = itemService.findItems();

		model.addAttribute("items", items);
		return "items/itemList";
	}

	// 상품 수정 페이지
	@GetMapping("/items/{itemId}/edit")
	public String showUpdateItemPage(@PathVariable("itemId") Long itemId, Model model) {
		Book item = (Book) itemService.findOne(itemId);
		EditItemResDTO form = EditItemResDTO.of(item);

		model.addAttribute("form", form);
		return "items/updateItemForm";
	}

	// 상품 수정 요청
	@PostMapping("/items/{itemId}/edit")
	public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") EditItemReqDTO dto, Model model) {
		Book book = Book.editOf(dto);
		itemService.saveItem(book);

		return "redirect:/items";
	}
}
