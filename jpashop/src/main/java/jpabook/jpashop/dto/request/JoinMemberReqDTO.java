package jpabook.jpashop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinMemberReqDTO {

	@NotBlank(message = "회원 이름은 필수 입니다.")
	private String name;

	private String city;

	private String street;

	private String zipcode;

}
