package jpabook.jpashop.dto.request;

import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderReqDTO {

	private Long orderId;

	private String name;

	private LocalDateTime orderDate;

	// Address 와 같은 Value 객체는 노출해도 된다. -> 엔티티가 아님
	private Address address;

	// 엔티티 노출 X -> OrderItem 도 엔티티 이므로 DTO 로 변환한다.
	private List<OrderItemReqDTO> orderItems;

	public OrderReqDTO(Order order) {
		this.orderId = order.getId();
		this.name = order.getMember().getName();
		this.orderDate = order.getOrderDate();
		this.address = order.getDelivery().getAddress();
		this.orderItems = order.getOrderItems().stream()
				.map(OrderItemReqDTO::new)
				.toList();
	}
}
