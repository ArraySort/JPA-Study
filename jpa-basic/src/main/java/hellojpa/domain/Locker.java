package hellojpa.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Locker {

	@Id
	@GeneratedValue
	@Column(name = "locker_id")
	private Long id;

	private String name;

	// 일대일 관계에서 연관관계 설정
	@OneToOne(mappedBy = "locker")
	private Member member;

}
