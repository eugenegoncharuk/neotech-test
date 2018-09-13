package com.neotech.task.app;

import com.neotech.task.controller.PhoneRequest;
import com.neotech.task.service.dto.CountryDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PhoneNumberCountryControllerITest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
		final PhoneRequest invalidLengthForLatvia = new PhoneRequest("3711231232");
		final PhoneRequest validLatvia = new PhoneRequest("37112312322");

		ResponseEntity<CountryDto> responseEntity =
				restTemplate.postForEntity("/country-detect", invalidLengthForLatvia, CountryDto.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

		ResponseEntity<CountryDto> responseEntity2 =
				restTemplate.postForEntity("/country-detect", validLatvia, CountryDto.class);
		CountryDto c = responseEntity2.getBody();
		assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
		assertEquals(c.getCountry(), "Latvia");
		assertEquals(c.getCountryCode(), "371");
	}

}
