package study.data_jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class JpaConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		// Spring Security 사용 시 세션 정보에서 ID 를 꺼내면 된다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
