package hellojpa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String name;

	private String city;

	private String street;

	private String zipcode;
	
	// 일대다 관계 적용 => 연관관계 주인 : 중간테이블(FK)
	@OneToMany(mappedBy = "member")
	private List<MemberProduct> memberProducts = new ArrayList<>();

	// 다대일 관계처럼 FK 가 있는 곳이 연관관계의 주인이 된다.
	@OneToOne
	@JoinColumn(name = "locker_id")
	private Locker locker;

	// 다대일 / 일대다 양방향 관계 설정 : FK 연관관계 주인(수정, 삭제 가능)
	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();

}
