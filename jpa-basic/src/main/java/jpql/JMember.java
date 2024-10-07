package jpql;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedQuery(
		name = "JMember.findByUsername",
		query = "select m from JMember m where m.username = :username")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private JTeam JTeam;

	@Override
	public String toString() {
		return "JMember{" +
				"id=" + id +
				", username='" + username + '\'' +
				", age=" + age +
				'}';
	}
}
