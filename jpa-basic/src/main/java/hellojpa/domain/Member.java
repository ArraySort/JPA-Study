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
public class Member extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String name;

	@Embedded
	private Address address;

	// 일대다 관계 적용 => 연관관계 주인 : 중간테이블(FK)
	@OneToMany(mappedBy = "member")
	private List<MemberProduct> memberProducts = new ArrayList<>();

	// 다대일 관계처럼 FK 가 있는 곳이 연관관계의 주인이 된다.
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "locker_id")
	private Locker locker;

	// 다대일 / 일대다 양방향 관계 설정 : FK 연관관계 주인(수정, 삭제 가능)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();

}
