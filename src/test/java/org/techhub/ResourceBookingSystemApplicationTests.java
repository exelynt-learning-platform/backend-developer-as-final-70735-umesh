package org.techhub;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.techhub.repository.ReservationRepository;

@SpringBootTest
@ActiveProfiles("test")
class ResourceBookingSystemApplicationTests {

	@Autowired
	private ReservationRepository reservationRepository;

	@Test
	void applicationContext_shouldLoad() {

		assertNotNull(reservationRepository);
	}

	@Test
	void reservationRepository_shouldBeAvailable() {

		long count = reservationRepository.count();

		System.out.println("Reservation count = " + count);
	}
}