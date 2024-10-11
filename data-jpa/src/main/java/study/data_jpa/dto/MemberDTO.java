package study.data_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MemberDTO {

	private Long id;

	private String username;

	private String teamName;

}
