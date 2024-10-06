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
public class Category extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "category_id")
	private Long id;

	private String name;

	// 계층형 구조를 위한 부모 객체, 리스트 생성 / Join
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parent;

	// 일대다 매핑 : 자기 자신 참조
	@OneToMany(mappedBy = "parent")
	private List<Category> child = new ArrayList<>();

	// 다대다 매핑 : 권장 X -> JoinColumn 중간 테이블 FK 지정
	@ManyToMany
	@JoinTable(name = "category_item",
			joinColumns = @JoinColumn(name = "category_id"),
			inverseJoinColumns = @JoinColumn(name = "item_id"))
	private List<Item> items = new ArrayList<>();

}
