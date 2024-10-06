package jpql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JProduct {

	@Id
	@GeneratedValue
	@Column(name = "product_id")
	private Long id;

	private String name;

	private int price;

	private int stockAmount;

}
