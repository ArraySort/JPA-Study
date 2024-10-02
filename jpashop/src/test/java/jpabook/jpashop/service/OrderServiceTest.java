package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.Entity.Order;
import jpabook.jpashop.Entity.enums.OrderStatus;
import jpabook.jpashop.Entity.item.Book;
import jpabook.jpashop.Entity.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class OrderServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Test
	@Transactional
	public void 상품주문() throws Exception {
		// given
		Member member = createMember();
		Book book = createBook("JPA", 10000, 10);
		int orderCount = 2;

		// when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		// then
		Order getOrder = orderRepository.findOne(orderId);

		Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문 시 상태 : ORDER");
		Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문 한 상품 주문수가 정확 해야한다.");
		Assertions.assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
		Assertions.assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
	}

	@Test
	@Transactional
	public void 상품주문_재고수량초과() throws Exception {
		// given
		Member member = createMember();
		Item item = createBook("JPA2", 1000, 9);
		int orderCount = 11;

		// when & then
		Assertions.assertThrows(NotEnoughStockException.class, () -> {
			orderService.order(member.getId(), item.getId(), orderCount);
		});
	}

	@Test
	@Transactional
	public void 주문취소() throws Exception {
		// given
		Member member = createMember();
		Book item = createBook("JPA", 10000, 10);

		int orderCount = 2;

		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

		// when
		orderService.cancelOrder(orderId);

		// then
		Order getOrder = orderRepository.findOne(orderId);
		Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소 시 상태 : CANCEL");
		Assertions.assertEquals(10, item.getStockQuantity(), "주문이 취소 된 상품은 그만큼 재고가 증가");
	}

	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("서울", "강가", "123-123"));
		em.persist(member);
		return member;
	}

	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}
}