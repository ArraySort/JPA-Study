package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	// 파라미터가 많아지면 이름이 엄청나게 길어지는 단점이 있음
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	// @Query(name = "Member.findByUsername") -> 없어도 동작함. 우선순위를 가짐 / 실무에서 거의 사용하지 않음
	List<Member> findByUsername(@Param("username") String username);

	// @Query 를 통한 메소드에 쿼리 정의 -> 실무에서 많이 씀
	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	// @Query 를 통한 컬럼 조회(결과 타입만 잘 적으면 된다)
	@Query("select m.username from Member m")
	List<String> findUsernameList();

	// JPQL DTO 조회를 통한 @Query 지정
	@Query("select new study.data_jpa.dto.MemberDTO(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDTO> findMemberReqDTO();

	// 파라미터 바인딩을 통한 In 절(이름 기반 바인딩)
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") List<String> names);

	// 컬렉션 조회
	List<Member> findMemberListByUsername(String username);

	// 단건 조회
	Member findMemberByUsername(String username);

	// Optional 단건 조회
	Optional<Member> findOptionalMemberByUsername(String username);

	// 페이징(카운트 쿼리 분리)
	@Query(value = "select m from Member m left join m.team t",
			countQuery = "select count(m.username) from Member m")
	Page<Member> findByAge(int age, Pageable pageable);

	// 벌크성 수정 쿼리
	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);

	@Query("select m from Member m left join fetch m.team")
	List<Member> findMemberFetchJoin();

	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	// 변경 감지가 되지 않도록 설정 : 잘 쓰지 않음
	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);

	// 락 기능 제공 : 비관적 락?
	@Lock(LockModeType.PESSIMISTIC_READ)
	List<Member> findLockByUsername(String username);
}
