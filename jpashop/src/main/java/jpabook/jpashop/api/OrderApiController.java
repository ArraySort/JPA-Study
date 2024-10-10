package jpabook.jpashop.api;

import jpabook.jpashop.Entity.Order;
import jpabook.jpashop.Entity.OrderItem;
import jpabook.jpashop.dto.request.OrderFlatDTO;
import jpabook.jpashop.dto.request.OrderItemQueryReqDTO;
import jpabook.jpashop.dto.request.OrderQueryReqDTO;
import jpabook.jpashop.dto.request.OrderReqDTO;
import jpabook.jpashop.repository.OrderQueryRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;

	private final OrderQueryRepository orderQueryRepository;

	// 엔티티를 직접 노출하기 때문에 사용하면 안됨.
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

		for (Order order : all) {
			order.getMember().getName();    // Member Lazy Loading 초기화
			order.getDelivery().getAddress();    // Delivery Lazy Loading 초기화

			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.forEach(o -> o.getItem().getName());    // OrderItem - Item Lazy Loading 초기화
		}
		return all;
	}

	/**
	 * SQL 실행 횟수(N + 1 문제 발생)
	 * 1. Order 조회 - 1 (2개 조회)
	 * 2. 주문자 조회 - 2 Member(userA, userB)
	 * 3. 배송정보 조회 - 2 Address(userA, userB)
	 * 4. 주문 상품 조회 - 4 (JPA1 Book, JPA2 Book, Spring1 Book, Spring2 Book)
	 * <p>
	 * 총 실행 횟수 : 9
	 */
	@GetMapping("/api/v2/orders")
	public List<OrderReqDTO> ordersV2() {
		List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

		return orders.stream()
				.map(OrderReqDTO::new)
				.toList();
	}

	/**
	 * SQL 실행 횟수
	 * 1. Order 조회 - 1 (2개 조회)
	 * 2. join 4번 발생 - Member, Delivery(Address), OrderItem, Item
	 * 3. fetch join 사용
	 * <p>
	 * Order 입장에서 join 이 발생하면 -> Item 에 따라서  총 4개가 만들어진다. (데이터 뻥튀기)
	 * Spring boot 3 / Hibernate 6 이상에서는 distinct 가 자동 적용된다.
	 * 페이징 불가
	 * 총 실행 횟수 : 1
	 */
	@GetMapping("/api/v3/orders")
	public List<OrderReqDTO> ordersV3() {
		List<Order> orders = orderRepository.findAllWithItem();

		for (Order order : orders) {
			System.out.println("order Id = " + order.getId() + " order ref = " + order);
		}

		return orders.stream()
				.map(OrderReqDTO::new)
				.toList();
	}

	/**
	 * 컬렉션은 페치 조인 시 페이징 불가능
	 * ToOne 관계는 페치 조인으로 쿼리 수 최적화
	 * 컬렉션은 페치 조인 대신에 지연 로딩을 유지하고 hibernate.default_batch_fetch_size, @BatchSize 로 최적화
	 */
	@GetMapping("/api/v3.1/orders")
	public List<OrderReqDTO> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
										   @RequestParam(value = "limit", defaultValue = "100") int limit) {
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

		for (Order order : orders) {
			System.out.println("order Id = " + order.getId() + " order ref = " + order);
		}

		return orders.stream()
				.map(OrderReqDTO::new)
				.toList();
	}

	// JPQL -> DTO 저장 : 상품에 대한 N + 1 문제 발생
	@GetMapping("/api/v4/orders")
	public List<OrderQueryReqDTO> ordersV4() {
		return orderQueryRepository.findOrderQueryDTOs();
	}

	// 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 In 절을 활용해서 메모리에서 미리 조회(쿼리 수 줄임)
	@GetMapping("/api/v5/orders")
	public List<OrderQueryReqDTO> ordersV5() {
		return orderQueryRepository.findAllByDTO_optimization();
	}

	// 플랫 데이터 최적화 - Join 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환(join)
	@GetMapping("/api/v6/orders")
	public List<OrderQueryReqDTO> ordersV6() {
		List<OrderFlatDTO> flats = orderQueryRepository.findAllByDTO_flat();

		return flats.stream()
				.collect(groupingBy(OrderQueryReqDTO::orderOf, mapping(OrderItemQueryReqDTO::orderItemOf, toList()))
				).entrySet().stream()
				.map(OrderQueryReqDTO::fromEntry)
				.toList();
	}
}
