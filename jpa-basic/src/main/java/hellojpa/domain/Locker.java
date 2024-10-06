package hellojpa.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Locker extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "locker_id")
	private Long id;

	private String name;

	// 일대일 관계에서 연관관계 설정
	@OneToOne(mappedBy = "locker", fetch = FetchType.LAZY)
	private Member member;

}
