package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.Entity.Address;
import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.dto.JoinMemberReqDTO;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/members/new")
	public String showJoinPage(Model model) {
		model.addAttribute("memberForm", new JoinMemberReqDTO());
		return "members/createMemberForm";
	}

	@PostMapping("/members/new")
	public String create(@Valid @ModelAttribute JoinMemberReqDTO dto, BindingResult bindingResult) {
		// 에러 발생 시 : 필수 값 입력 안했을 시
		if (bindingResult.hasErrors()) {
			return "members/createMemberForm";
		}

		Address address = Address.of(dto);
		Member member = new Member();

		member.setName(dto.getName());
		member.setAddress(address);

		// 회원가입
		memberService.join(member);

		return "redirect:/";
	}

	@GetMapping("/members")
	public String showMemberListPage(Model model) {
		List<Member> members = memberService.findMembers();

		model.addAttribute("members", members);
		return "members/memberList";
	}
}
