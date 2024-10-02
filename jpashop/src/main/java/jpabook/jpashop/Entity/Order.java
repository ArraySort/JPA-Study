package jpabook.jpashop.Entity;

import jakarta.persistence.*;
import jpabook.jpashop.Entity.enums.DeliveryStatus;
import jpabook.jpashop.Entity.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;

	// 다대일 관계 설정
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;    // 회원

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();    // 주문 상품 리스트

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;    // 배송

	// JAVA 8 에서는 Hibernate 가 LocalDateTime 을 지원해준다.
	private LocalDateTime orderDate;    // 주문일자

	@Enumerated(EnumType.STRING)
	private OrderStatus status;    // 주문상태 [ORDER, CANCEL]

	// 주문 생성
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);

		for (OrderItem orderItem : orderItems) {
			order.addOrderItem(orderItem);
		}

		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());

		return order;
	}

	// 주문 취소, 재고 상태 원복
	public void cancel() {
		if (delivery.getStatus() == DeliveryStatus.COMP) {
			throw new IllegalStateException("이미 배송완료 된 상품은 취소가 불가능합니다.");
		}

		this.setStatus(OrderStatus.CANCEL);

		orderItems.forEach(OrderItem::cancel);
	}

	// 전체 주문 가격 조회
	public int getTotalPrice() {
		return orderItems.stream()
				.mapToInt(OrderItem::getTotalPrice)
				.sum();
	}

	//== 연관관계 메서드 ==//
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}

	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}
}
