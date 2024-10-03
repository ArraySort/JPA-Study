package hellojpa.hello;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class TestMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;    // PK 매핑, GeneratedValue -> [TABLE, SEQUENCE, IDENTITY, UUID, AUTO]

	@Column(name = "name")
	private String username;    // 컬럼 이름 매핑

	private Integer age;

	@Enumerated(EnumType.STRING)
	private RoleType roleType;    // enumType 매핑

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;    // 데이터베이스 사용 날짜 매핑

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;    // 데이터베이스 사용 날짜 매핑

	private LocalDate testLocalDate;    // Java 8 이상, 최신 하이버네이트에서는 위에 어노테이션이 필요없음

	private LocalDateTime testLocalDateTime;    // JPA 가 자바의 타입을 보고 Date 와 TimeStamp 로 매핑시켜줌

	@Lob
	private String description;    // 대용량 VARCHAR 를 넘어선 데이터 저장 [BLOB, CLOB : 문자타입]

	@Transient
	private String temp;    // 메모리에서 사용되는 것 매핑하고 싶지 않은 컬럼

	protected TestMember() {

	}
}
