package jpabook.jpashop.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderItemQueryReqDTO {

	@JsonIgnore
	private Long orderId;

	private String itemName;

	private int orderPrice;

	private int count;

	// orderItem 생성
	public static OrderItemQueryReqDTO orderItemOf(OrderFlatDTO flat) {
		return OrderItemQueryReqDTO.builder()
				.orderId(flat.getOrderId())
				.itemName(flat.getItemName())
				.count(flat.getCount())
				.build();
	}
}
