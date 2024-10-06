package jpql;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JTeam {

	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;

	private String name;

	@OneToMany(mappedBy = "JTeam")
	private List<JMember> JMembers = new ArrayList<>();

}
