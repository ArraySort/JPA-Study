package jpabook.jpashop.dto.request;

import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "orderId")
public class OrderQueryReqDTO {

	private Long orderId;

	private String name;

	private LocalDateTime orderDate;

	private OrderStatus orderStatus;

	private Address address;

	private List<OrderItemQueryReqDTO> orderItems;

	public OrderQueryReqDTO(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.address = address;
	}

	// order 생성
	public static OrderQueryReqDTO orderOf(OrderFlatDTO flat) {
		return OrderQueryReqDTO.builder()
				.orderId(flat.getOrderId())
				.name(flat.getName())
				.orderDate(flat.getOrderDate())
				.orderStatus(flat.getOrderStatus())
				.address(flat.getAddress())
				.build();
	}

	// order - orderItem Entry
	public static OrderQueryReqDTO fromEntry(Map.Entry<OrderQueryReqDTO, List<OrderItemQueryReqDTO>> entry) {
		OrderQueryReqDTO key = entry.getKey();
		return new OrderQueryReqDTO(
				key.getOrderId(),
				key.getName(),
				key.getOrderDate(),
				key.getOrderStatus(),
				key.getAddress(),
				entry.getValue()
		);
	}
}
