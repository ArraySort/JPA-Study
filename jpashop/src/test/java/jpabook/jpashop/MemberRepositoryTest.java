package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	@Transactional
	@Rollback(value = false)
	void testMember() throws Exception {
		// 테스트 케이스에 Transactional 을 붙일 경우 반복적인 테스트를 위해서 테스트 종료 시 롤백 된다.

		// given
		Member member = new Member();
		member.setUsername("memberA");

		// when
		Long savedId = memberRepository.save(member);
		Member findMember = memberRepository.find(savedId);

		// then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

		// findMember 로 찾은 값이 같은가?
		// 같은 영속성 컨텍스트 안에서 id 값이 같으면 같은 엔티티로 인식한다.
		Assertions.assertThat(findMember).isEqualTo(member);
	}
}