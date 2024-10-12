package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;

import java.util.List;
import java.util.Optional;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<MemberTeamDTO> search(MemberSearchCondition condition) {
		return queryFactory
				.select(Projections.constructor(
						MemberTeamDTO.class,
						member.id.as("memberId"),
						member.username,
						member.age,
						team.id.as("teamId"),
						team.name.as("teamName")))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()),
						ageLoe(condition.getAgeLoe())
				)
				.fetch();
	}

	@Override
	public Page<MemberTeamDTO> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
		QueryResults<MemberTeamDTO> results = queryFactory
				.select(Projections.constructor(
						MemberTeamDTO.class,
						member.id.as("memberId"),
						member.username,
						member.age,
						team.id.as("teamId"),
						team.name.as("teamName")))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()),
						ageLoe(condition.getAgeLoe())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		List<MemberTeamDTO> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<MemberTeamDTO> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
		List<MemberTeamDTO> content = queryFactory
				.select(Projections.constructor(
						MemberTeamDTO.class,
						member.id.as("memberId"),
						member.username,
						member.age,
						team.id.as("teamId"),
						team.name.as("teamName")))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()),
						ageLoe(condition.getAgeLoe())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = queryFactory
				.select(member.countDistinct())
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()),
						ageLoe(condition.getAgeLoe())
				)
				.fetchOne();

		return new PageImpl<>(content, pageable, Optional.ofNullable(total).orElse(0L));
	}

	private BooleanExpression usernameEq(String username) {
		return StringUtils.hasText(username) ? member.username.eq(username) : null;
	}

	private BooleanExpression teamNameEq(String teamName) {
		return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
	}

	private BooleanExpression ageGoe(Integer ageGoe) {
		return ageGoe != null ? member.age.goe(ageGoe) : null;
	}

	private BooleanExpression ageLoe(Integer ageLoe) {
		return ageLoe != null ? member.age.loe(ageLoe) : null;
	}
}
