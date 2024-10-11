package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Rollback(value = false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	@Transactional
	void testMember() {
		// given
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		// when
		Member findMember = memberRepository.findById(savedMember.getId()).get();

		// then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member);
	}

	@Test
	@Transactional
	void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");

		memberRepository.save(member1);
		memberRepository.save(member2);

		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();

		// 데이터 변경 확인
		findMember1.changeUsername("member Changed!");
		System.out.println("=== findMember1 === : " + findMember1);

		// 단건 조회 검증
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> allMember = memberRepository.findAll();
		assertThat(allMember).hasSize(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);

		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isZero();
	}

	@Test
	@Transactional
	void findByUsernameAndAgeGreaterThen() {
		Member member1 = new Member("member", 10);
		Member member2 = new Member("member", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("member", 15);
		assertThat(result.get(0).getUsername()).isEqualTo("member");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result).hasSize(1);
	}

	@Test
	@Transactional
	void namedQuery() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findByUsername("member1");
		assertThat(result.get(0)).isEqualTo(member1);
	}

	@Test
	@Transactional
	void methodQuery() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findUser("member1", 10);
		assertThat(result.get(0)).isEqualTo(member1);
	}

	@Test
	@Transactional
	void columnQuery() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<String> result = memberRepository.findUsernameList();

		for (String s : result) {
			System.out.println("s = " + s);
		}

		assertThat(result.get(0)).isEqualTo(member1.getUsername());
	}


	@Test
	@Transactional
	void dtoQuery() {
		Team team = new Team("teamA");
		teamRepository.save(team);

		Member member1 = new Member("member1", 10);
		member1.changeTeam(team);
		memberRepository.save(member1);

		List<MemberDTO> result = memberRepository.findMemberReqDTO();

		for (MemberDTO memberDTO : result) {
			System.out.println("memberDTO = " + memberDTO);
		}

		assertThat(result.get(0).getUsername()).isEqualTo(member1.getUsername());
	}

	@Test
	@Transactional
	void findByNames() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findByNames(Arrays.asList("member1", "member2"));

		for (Member member : result) {
			System.out.println("member = " + member);
		}
	}

	@Test
	@Transactional
	void returnType() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		memberRepository.findMemberListByUsername("member1");
		memberRepository.findMemberByUsername("member1");
		memberRepository.findOptionalMemberByUsername("member1").get();
	}

	@Test
	@Transactional
	void paging() {
		int age = 10;

		for (int i = 1; i < 11; i++) {
			memberRepository.save(new Member("member" + i, 10));
		}

		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
		Page<Member> page = memberRepository.findByAge(age, pageRequest);
		Page<MemberDTO> toMap = page.map(m -> new MemberDTO(m.getId(), m.getUsername(), null));

		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();

		for (Member member : content) {
			System.out.println("member = " + member);
		}

		System.out.println("totalElements = " + totalElements);

		// limit
		assertThat(content).hasSize(3);
		// totalCount
		assertThat(page.getTotalElements()).isEqualTo(10);
		// Number -> 0
		assertThat(page.getNumber()).isZero();
		// totalPages -> 10 / 3 -> 3.xx.. = 4
		assertThat(page.getTotalPages()).isEqualTo(4);
		// first Page -> true
		assertThat(page.isFirst()).isTrue();
		// next Page -> true
		assertThat(page.hasNext()).isTrue();
	}

	@Test
	@Transactional
	void bulkUpdate() {
		for (int i = 1; i < 6; i++) {
			memberRepository.save(new Member("member" + i, 20));
		}

		int resultCount = memberRepository.bulkAgePlus(20);
		assertThat(resultCount).isEqualTo(5);
	}

	@Test
	@Transactional
	void findMemberLazy() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);

		em.flush();
		em.clear();

		// N + 1
		// select Member : 1
		List<Member> members = memberRepository.findAll();

		// select Team : N
		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.getClass() = " + member.getClass());
			System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
		}
	}

	@Test
	@Transactional
	void queryHint() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// 변경 감지 안됨
		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.changeUsername("member2");

		em.flush();
	}

	@Test
	@Transactional
	void lock() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// 변경 감지 안됨
		List<Member> findMember = memberRepository.findLockByUsername("member1");
	}
}