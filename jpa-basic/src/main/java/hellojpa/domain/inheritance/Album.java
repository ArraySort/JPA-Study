package hellojpa.domain.inheritance;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("A")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends ProductItem {

	private String artist;

	private String etc;

}
