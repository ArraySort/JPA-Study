package jpabook.jpashop.api;

import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.Order;
import jpabook.jpashop.Entity.enums.OrderStatus;
import jpabook.jpashop.dto.OrderSimpleQueryDTO;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * XToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member(ManyToOne)
 * Order -> Delivery(OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;

	/**
	 * 엔티티를 외부로 노출하면 엔티티의 수정이 일어나고 API 스펙이 바뀌어 버린다.
	 * Hibernate 라이브러리를 통해서 LAZY Loading 을 강제로 다 끌고 온다. -> 성능 문제
	 * 엔티티를 직접 노출하는 것은 사용 하면 안된다.
	 * <p>
	 * 프록시 객체 초기화 이후 값을 불러옴으로써 Lazy 로딩을 강제 초기화한다.
	 */
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
		for (Order order : all) {
			order.getMember().getName(); // Lazy 로딩 강제 초기화
			order.getDelivery().getAddress(); // Lazy 로딩 강제 초기화
		}
		return all;
	}

	// 엔티티 -> DTO 변환 API
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDTO> ordersV2() {
		// 엔티티 조회 : 2개
		// N + 1 문제 -> 1 + 회원 N + 배송 N -> 5번 쿼리
		List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

		// 엔티티 -> DTO 변환
		return orders.stream()
				.map(SimpleOrderDTO::new)
				.toList();
	}

	// join fetch 를 통한 조회 -> 1번 쿼리
	// 범용성 있게 다른 곳에서도 쓸 수 있음
	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDTO> ordersV3() {
		List<Order> orders = orderRepository.findAllWithMemberDelivery();

		return orders.stream()
				.map(SimpleOrderDTO::new)
				.toList();
	}

	// DTO 를 통한 매핑 -> DTO 에 맞게 설계되어 select 하는 컬럼 조정 가능 / 성능 최적화
	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDTO> ordersV4() {
		return orderRepository.findOrderDTOs();
	}

	// 주문 DTO
	@Data
	static class SimpleOrderDTO {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;

		public SimpleOrderDTO(Order order) {
			this.orderId = order.getId();
			this.name = order.getMember().getName();    // LAZY 초기화
			this.orderDate = order.getOrderDate();
			this.orderStatus = order.getStatus();
			this.address = order.getDelivery().getAddress();    // LAZY 초기화
		}
	}
}
