package jpabook.jpashop.dto.request;

import jpabook.jpashop.Entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemReqDTO {

	private String itemName;    // 상품명

	private int orderPrice;    // 상품 가격

	private int count;    // 주문 수량


	public OrderItemReqDTO(OrderItem orderItem) {
		this.itemName = orderItem.getItem().getName();    // OrderItem -> Item.name
		this.orderPrice = orderItem.getOrderPrice();
		this.count = orderItem.getCount();
	}
}
