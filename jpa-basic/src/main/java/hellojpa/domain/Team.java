package hellojpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {

	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;

	private String name;

	// 다대일 / 일대다 양방향 관계 설정 : 조회만 가능
	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

}
