package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

	// Hibernate5JakartaModule 빈 등록
	@Bean
	Hibernate5JakartaModule hibernate5JakartaModule() {
		Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
		// 지연 로딩 강제 : 권장하지 않음
		// hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);

		return hibernate5JakartaModule;
	}
}
