package study.data_jpa.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})    // team 이 들어가면 무한루프가 된다.
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;    // PK 설정

	private String username;    // 회원 이름

	private int age;    // 회원 나이

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;    // 회원 소속 팀 : N(Member) : 1(Team) 관계, 양방향 연관관계(주인)

	public Member(String username) {
		this.username = username;
	}

	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;

		if (team != null) {
			changeTeam(team);
		}
	}

	// 양방향 연관관계 편의 메서드
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}
