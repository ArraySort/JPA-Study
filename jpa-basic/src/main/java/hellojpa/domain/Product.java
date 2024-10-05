package hellojpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	// 일대다 관계 적용 => 연관관계 주인 : 중간테이블(FK)
	@OneToMany(mappedBy = "product")
	private List<MemberProduct> memberProducts = new ArrayList<>();

}
