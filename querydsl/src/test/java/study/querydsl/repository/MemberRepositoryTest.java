package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

	@Autowired
	EntityManager em;

	@Autowired
	MemberRepository memberRepository;

	@Test
	void 기본_테스트() {
		Member member = new Member("member1", 10);
		memberRepository.save(member);

		Member findMember = memberRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);

		List<Member> result1 = memberRepository.findAll();
		assertThat(result1).containsExactly(member);

		List<Member> result2 = memberRepository.findByUsername(member.getUsername());
		assertThat(result2).containsExactly(member);
	}

	@Test
	void 검색_테스트() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		MemberSearchCondition condition = new MemberSearchCondition();
		condition.setAgeGoe(35);
		condition.setAgeLoe(40);
		condition.setTeamName("teamB");

		List<MemberTeamDTO> result = memberRepository.search(condition);

		assertThat(result).extracting("username").containsExactly("member4");
	}

	@Test
	void 검색_단순_페이징_테스트() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		MemberSearchCondition condition = new MemberSearchCondition();

		PageRequest pageRequest = PageRequest.of(0, 3);

		Page<MemberTeamDTO> simple = memberRepository.searchPageSimple(condition, pageRequest);
		Page<MemberTeamDTO> complex = memberRepository.searchPageComplex(condition, pageRequest);

		assertThat(simple.getSize()).isEqualTo(3);
		assertThat(simple).extracting("username").containsExactly("member1", "member2", "member3");

		assertThat(complex.getSize()).isEqualTo(3);
		assertThat(complex).extracting("username").containsExactly("member1", "member2", "member3");
	}
}