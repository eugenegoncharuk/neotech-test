package com.neotech.task.controller;

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
	public void validPhoneNumbersTest() {
		checkIsValid(new PhoneRequest("37112312322"), "Latvia", "371");
		checkIsValid(new PhoneRequest("13321231212332"), "United States", "1332");
		checkIsValid(new PhoneRequest("6831234"), "Niue", "683");
	}

	@Test
	public void incorrectLengthPhoneNumberTest() {
		checkIncorrectLengthFor(new PhoneRequest("3712665332"));
		checkIncorrectLengthFor(new PhoneRequest("371266533222"));
		checkIncorrectLengthFor(new PhoneRequest("37512345678"));
		checkIncorrectLengthFor(new PhoneRequest("3751234567890"));
		checkIncorrectLengthFor(new PhoneRequest("68345623"));
	}

	@Test
	public void notValidPhoneNumberTest() {
		checkIsNotValidFor(new PhoneRequest(""));
		checkIsNotValidFor(new PhoneRequest("12"));
		checkIsNotValidFor(new PhoneRequest("134533"));
		checkIsNotValidFor(new PhoneRequest("12345678901234567"));
		checkIsNotValidFor(new PhoneRequest("1234567890123456732423223443243232323423"));
		checkIsNotValidFor(new PhoneRequest("somestring"));
		checkIsNotValidFor(new PhoneRequest("some long text, not related to phone number"));
	}


	private void checkIncorrectLengthFor(PhoneRequest req) {
		ResponseEntity<CountryDto> responseInvalid =
				restTemplate.postForEntity("/country-detect", req, CountryDto.class);
		assertEquals(HttpStatus.NOT_FOUND, responseInvalid.getStatusCode());
	}

	private void checkIsValid(PhoneRequest validLatvia, String country, String code) {
		ResponseEntity<CountryDto> responseValid =
				restTemplate.postForEntity("/country-detect", validLatvia, CountryDto.class);
		CountryDto c = responseValid.getBody();
		assertEquals(HttpStatus.OK, responseValid.getStatusCode());
		assertEquals(c.getCountry(), country);
		assertEquals(c.getCountryCode(), code);
	}

	private void checkIsNotValidFor(PhoneRequest req) {
		ResponseEntity<CountryDto> responseInvalid =
				restTemplate.postForEntity("/country-detect", req, CountryDto.class);
		assertEquals(HttpStatus.BAD_REQUEST, responseInvalid.getStatusCode());
	}
}
