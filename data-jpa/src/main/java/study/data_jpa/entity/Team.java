package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;    // PK 설정

	private String name;    // team 이름

	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();    // 1(team) : N (Member) 관계, 양방향 연관관계(조회)

	public Team(String name) {
		this.name = name;
	}
}
