package jpabook.jpashop.service;

import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	// 회원가입
	@Transactional
	public Long join(Member member) {
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getId();
	}

	// 회원 조회(전체 리스트)
	@Transactional(readOnly = true)
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}

	// 회원 조회(Id)
	@Transactional(readOnly = true)
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}

	/**
	 * 중복 회원 검증
	 *
	 * @param member 검증하려는 회원 객체
	 */
	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());

		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}
}
