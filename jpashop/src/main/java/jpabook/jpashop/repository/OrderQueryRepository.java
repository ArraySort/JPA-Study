package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.dto.request.OrderFlatDTO;
import jpabook.jpashop.dto.request.OrderItemQueryReqDTO;
import jpabook.jpashop.dto.request.OrderQueryReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

	private final EntityManager em;

	// N + 1 문제 발생
	public List<OrderQueryReqDTO> findOrderQueryDTOs() {
		List<OrderQueryReqDTO> result = findOrders();

		result.forEach(o -> {
			List<OrderItemQueryReqDTO> orderItems = findOrderItems(o.getOrderId());
			o.setOrderItems(orderItems);
		});

		return result;
	}


	// N + 1 문제 해결 : in 절을 통한 하위 상품 전부 조회 - Map 메모리 활용
	public List<OrderQueryReqDTO> findAllByDTO_optimization() {
		List<OrderQueryReqDTO> result = findOrders();

		List<Long> orderIds = toOrderIds(result);

		Map<Long, List<OrderItemQueryReqDTO>> orderItemMap = findOrderItemMap(orderIds);

		result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

		return result;
	}

	// DTO 직접 조회 최적화 -> Join 을 통한 쿼리 횟수 1회
	public List<OrderFlatDTO> findAllByDTO_flat() {
		return em.createQuery("select new " +
						"jpabook.jpashop.dto.request.OrderFlatDTO(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
						" from Order o" +
						" join o.member m" +
						" join o.delivery d" +
						" join o.orderItems oi" +
						" join oi.item i", OrderFlatDTO.class)
				.getResultList();
	}

	private Map<Long, List<OrderItemQueryReqDTO>> findOrderItemMap(List<Long> orderIds) {
		List<OrderItemQueryReqDTO> orderItems = em.createQuery(
						"select new jpabook.jpashop.dto.request.OrderItemQueryReqDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
								" from OrderItem oi" +
								" join oi.item i" +
								" where oi.order.id in :orderIds", OrderItemQueryReqDTO.class)
				.setParameter("orderIds", orderIds)
				.getResultList();

		return orderItems.stream()
				.collect(Collectors.groupingBy(OrderItemQueryReqDTO::getOrderId));
	}

	private List<Long> toOrderIds(List<OrderQueryReqDTO> result) {
		return result.stream()
				.map(OrderQueryReqDTO::getOrderId)
				.toList();
	}

	private List<OrderItemQueryReqDTO> findOrderItems(Long orderId) {
		return em.createQuery(
						"select new jpabook.jpashop.dto.request.OrderItemQueryReqDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
								" from OrderItem oi" +
								" join oi.item i" +
								" where oi.order.id = :orderId", OrderItemQueryReqDTO.class)
				.setParameter("orderId", orderId)
				.getResultList();
	}

	private List<OrderQueryReqDTO> findOrders() {
		return em.createQuery(
						"select new jpabook.jpashop.dto.request.OrderQueryReqDTO(o.id, m.name, o.orderDate, o.status, d.address)" +
								" from Order o" +
								" join o.member m" +
								" join o.delivery d", OrderQueryReqDTO.class)
				.getResultList();
	}
}
