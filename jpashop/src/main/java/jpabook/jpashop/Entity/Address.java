package jpabook.jpashop.Entity;

import jakarta.persistence.Embeddable;
import jpabook.jpashop.dto.request.JoinMemberReqDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Embeddable
public class Address {

	private String city;

	private String street;

	private String zipcode;

	// 값 타입은 변경 불가능하게 설계해야 한다.
	// JPA 스펙상 엔티티나 @Embeddable 타입은 기본 생성자를 설정해야한다. -> 구현 라이브러리가 리플렉션 기술 사용할 수 있도록 지원해야하기 때문이다.
	// Utility 클래스를 사용하는 원리와 비슷하다??
	protected Address() {

	}

	// 기본생성자
	public Address(String city, String street, String zipcode) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}

	// 생성
	public static Address of(JoinMemberReqDTO dto) {
		return Address.builder()
				.city(dto.getCity())
				.street(dto.getStreet())
				.zipcode(dto.getZipcode())
				.build();
	}
}
