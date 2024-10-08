package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jpabook.jpashop.Entity.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	/**
	 * 조회 V1 : 응답  값으로 엔티티를 직접 외부에 노출
	 * 문제점
	 * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
	 * - 기본적으로 엔티티의 모든 값이 노출된다.
	 * - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직)
	 * - 실무에서 같은 엔티티에 대한 API 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API 를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
	 * - 엔티티가 변경되면 API 스펙이 변한다.
	 * - 추가로 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
	 * 결론
	 * - API 응답 스펙에 맞추어 별도의 DTO 를 반환한다.
	 */
	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		return memberService.findMembers();
	}

	// Result 를 통한 오브젝트로 반환, DTO 로 변환을 통한 API 와 DTO 의 1대1 매핑
	@GetMapping("/api/v2/members")
	public Result memberV2() {
		List<Member> findMembers = memberService.findMembers();
		List<MemberListResponseDTO> memberList = findMembers.stream()
				.map(member -> new MemberListResponseDTO(member.getName()))
				.toList();

		return new Result(memberList.size(), memberList);
	}

	// 엔티티를 바인딩하는 경우 : API 스펙이 바뀔 수 있음
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	// 회원 가입
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	// 회원 정보 수정
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
											   @RequestBody @Valid UpdateMemberRequest request) {

		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);
		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}

	@Data
	@AllArgsConstructor
	static class Result<T> {
		private int count;
		private T data;
	}

	@Data
	@AllArgsConstructor
	static class MemberListResponseDTO {
		private String name;
	}

	@Data
	static class UpdateMemberRequest {
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse {
		private Long id;
		private String name;
	}

	@Data
	static class CreateMemberRequest {
		@NotBlank
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class CreateMemberResponse {
		private Long id;
	}
}
