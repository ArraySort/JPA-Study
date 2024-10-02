package jpabook.jpashop.service;

import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberService memberService;

	@Test
	public void 회원가입() {
		// given
		Member member = new Member();
		member.setName("회원");

		// when
		Long savedId = memberService.join(member);

		// then
		Assertions.assertEquals(member, memberRepository.findOne(savedId));
	}

	@Test
	public void 중복_회원_예외() {
		// given
		Member member1 = new Member();
		member1.setName("중복회원");

		Member member2 = new Member();
		member2.setName("중복회원");

		// when
		memberService.join(member1);

		// then
		Assertions.assertThrows(IllegalStateException.class, () -> {
			memberService.join(member2);    // 중복 회원에 대한 예외 발생
		});
	}
}