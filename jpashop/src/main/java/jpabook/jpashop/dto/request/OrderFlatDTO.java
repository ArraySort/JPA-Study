package jpabook.jpashop.dto.request;

import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderFlatDTO {

	// orderDTO
	private Long orderId;

	private String name;

	private LocalDateTime orderDate;

	private OrderStatus orderStatus;

	private Address address;

	// orderItemDTO
	private String itemName;

	private int orderPrice;

	private int count;

}
