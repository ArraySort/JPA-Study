package hellojpa.domain.inheritance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ProductItem {

	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;

	private int price;

}
