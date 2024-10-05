package hellojpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "delivery_id")
	private Long id;

	private String city;

	private String street;

	private String zipcode;

	private DeliveryStatus status;

	@OneToOne(mappedBy = "delivery")
	private Order order;
}
