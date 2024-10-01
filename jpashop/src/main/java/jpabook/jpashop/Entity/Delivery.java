package jpabook.jpashop.Entity;

import jakarta.persistence.*;
import jpabook.jpashop.Entity.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

	@Id
	@GeneratedValue
	@Column(name = "delivery_id")
	private Long id;

	@OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
	private Order order;

	@Embedded
	private Address address;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus status; // [READY, COMP]

}
