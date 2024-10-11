package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id).get();
		return member.getUsername();
	}

	// 도메인 클래스 인버터 -> 권장하지 않음(조회용으로 사용해야한다. / 예외가 너무 많음)
	@GetMapping("/members2/{id}")
	public String findMember2(@PathVariable("id") Member member) {
		return member.getUsername();
	}

	@GetMapping("/members")
	public Page<MemberDTO> list(Pageable pageable) {
		return memberRepository.findAll(pageable)
				.map(m -> new MemberDTO(m.getId(), m.getUsername(), null));

	}

	@PostConstruct
	public void init() {
		memberRepository.save(new Member("userA"));

		for (int i = 0; i < 100; i++) {
			memberRepository.save(new Member("member" + i, i));
		}
	}
}
