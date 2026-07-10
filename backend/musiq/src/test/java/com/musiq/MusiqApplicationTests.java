package com.musiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"jwt.secret=ci-test-secret-that-is-long-enough-for-hmac-sha256",
	"jwt.expiration=86400000",
	"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"SPOTIFY_CLIENT_ID=fake-client-id",
	"SPOTIFY_CLIENT_SECRET=fake-client-secret",
	"app.frontend-url=http://localhost:5173"
})
class MusiqApplicationTests {

	@Test
	void contextLoads() {
	}

}
