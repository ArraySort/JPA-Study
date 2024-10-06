package jpql;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JOrder {

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;

	private int orderAmount;

	@Embedded
	private JAddress address;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private JProduct product;

}
