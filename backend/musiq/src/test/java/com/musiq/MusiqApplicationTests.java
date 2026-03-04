package com.musiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MusiqApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("${SPRING_DATASOURCE_URL}");
	}

}
