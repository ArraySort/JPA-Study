package hellojpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;

	private int price;

	private int stockQuantity;

	// 다대다 매핑 -> N : M 관계에서 N : 1 관계로 가정
	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();
}
