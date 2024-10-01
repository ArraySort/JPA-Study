package jpabook.jpashop.Entity;

import jakarta.persistence.*;
import jpabook.jpashop.Entity.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
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
