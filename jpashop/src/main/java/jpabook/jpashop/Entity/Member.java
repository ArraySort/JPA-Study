package jpabook.jpashop.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String name;        // 회원 이름

	@Embedded
	private Address address;    // 회원 주소

	// 일대다 관계 설정(연관관계 주인 설정, 읽기 전용)
	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();    // 주문
	
}
