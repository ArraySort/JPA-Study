package hellojpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Embeddable
@Getter
public class Address {

	@Column(length = 10)
	private String city;

	@Column(length = 20)
	private String street;

	@Column(length = 5)
	private String zipcode;

	// 비즈니스에 사용될 수 있는 의미있는 메서드 정의 가능
	private String fullAddress() {
		return getCity() + " " + getStreet() + " " + getZipcode();
	}

	// 참조 타입의 값 비교, Getter 방식을 사용하는 이유는 프록시 일 떄도 작동하기 위함
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Address address = (Address) o;
		return Objects.equals(getCity(), address.getCity()) &&
				Objects.equals(getStreet(), address.getStreet()) &&
				Objects.equals(getZipcode(), address.getZipcode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCity(), getStreet(), getZipcode());
	}
}