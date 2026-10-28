package com.bancodobrasil.residencia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"security.jwt.secret=0123456789abcdef0123456789abcdef-teste",
		"spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1"
})
class ResidenciaApplicationTests {

	@Test
	void contextLoads() {
	}

}
