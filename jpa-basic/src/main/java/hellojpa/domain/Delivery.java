package hellojpa.domain;

import jakarta.persistence.*;
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

	private String city;

	private String street;

	private String zipcode;

	private DeliveryStatus status;

	@OneToOne(mappedBy = "delivery")
	private Order order;
}
