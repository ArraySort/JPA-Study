package study.querydsl.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberTeamDTO {

	private Long memberId;

	private String username;

	private int age;

	private Long teamId;

	private String teamName;

}
