package jpql;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JMember {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String username;

	private int age;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private JTeam JTeam;

}
