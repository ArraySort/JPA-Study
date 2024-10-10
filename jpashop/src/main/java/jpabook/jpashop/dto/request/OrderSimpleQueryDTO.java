package jpabook.jpashop.dto.request;

import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSimpleQueryDTO {

	private Long orderId;

	private String name;

	private LocalDateTime orderDate;

	private OrderStatus orderStatus;

	private Address address;

}
