package jpabook.jpashop.controller;

import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.Entity.Order;
import jpabook.jpashop.Entity.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	private final MemberService memberService;

	private final ItemService itemService;

	// 주문 페이지
	@GetMapping("/order")
	public String showOrderPage(Model model) {
		List<Member> members = memberService.findMembers();
		List<Item> items = itemService.findItems();

		model.addAttribute("members", members);
		model.addAttribute("items", items);
		return "order/orderForm";
	}

	// 주문 요청 페이지
	@PostMapping("/order")
	public String order(@RequestParam("memberId") Long memberId,
						@RequestParam("itemId") Long itemId,
						@RequestParam("count") int count) {
		orderService.order(memberId, itemId, count);

		return "redirect:/orders";
	}

	// 주문 내역 조회 페이지
	@GetMapping("/orders")
	public String showOrderListPage(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
		List<Order> orders = orderService.findOrders(orderSearch);

		model.addAttribute("orders", orders);
		return "order/orderList";
	}

	// 주문 내역 취소 요청
	@PostMapping("/orders/{orderId}/cancel")
	public String cancelOrder(@PathVariable("orderId") Long orderId) {
		orderService.cancelOrder(orderId);

		return "redirect:/orders";
	}
}
