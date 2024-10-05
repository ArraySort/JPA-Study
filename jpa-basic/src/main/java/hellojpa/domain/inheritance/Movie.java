package hellojpa.domain.inheritance;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends ProductItem {

	private String director;

	private String actor;

}
