package com.Giga_JAD.Washer_Washer.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/payments")
public class CreateCheckOutSession {

	private final RestTemplate restTemplate;

	@Autowired
	public CreateCheckOutSession() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/checkout")
	public ResponseEntity<?> createCheckOutSession(@RequestBody Map<String, Object> requestBody, HttpSession session) {
		if (session == null || session.getAttribute("currentTrackedService") == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Session Expired. Please log in again."));
		}

		Map<String, Integer> booking_service_status_id = (Map<String, Integer>) session
				.getAttribute("currentTrackedService");
		if (booking_service_status_id == null || !booking_service_status_id.containsKey("service_id")) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid session data."));
		}

		// Validate request body
		if (!requestBody.containsKey("booking_id") || !requestBody.containsKey("amount")) {
			return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields: booking_id and amount."));
		}

		int bookingId = (int) requestBody.get("booking_id");
		double amount = Double.parseDouble(requestBody.get("amount").toString());

		try {
			// Get user credentials from session
			Map<String, Object> bookingDetails = getBookingDetails(booking_service_status_id.get("service_id"),
					bookingId);
			if (bookingDetails == null || !bookingDetails.containsKey("username")
					|| !bookingDetails.containsKey("password")) {
				return ResponseEntity.badRequest().body(Map.of("error", "Booking details not found."));
			}

			// External API URL
			String restUrl = "https://jad-wapi-wapi-ca2.onrender.com/wapi-wapi/payments/checkout";

			// Request Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("X-Username", "TestBusinessAcc");
			headers.set("X-Secret", "123");
			headers.set("X-third-party", "true");

			// Request Body as a List of Maps
			List<Map<String, Object>> payloadList = new ArrayList<>();
			Map<String, Object> payload = new HashMap<>();
			payload.put("bookingId", bookingId);
			payload.put("amount", amount);
			payload.put("currency", "SGD");
			payload.put("quantity", 1);
			payloadList.add(payload); // Wrapping in a list

			HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(payloadList, headers);

			// Make External API Call
			ResponseEntity<Map> response = restTemplate.exchange(restUrl, HttpMethod.POST, requestEntity, Map.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				Map<String, Object> responseBody = response.getBody();
				if (responseBody.containsKey("sessionUrl")) {
					return ResponseEntity.ok(Map.of("redirectUrl", responseBody.get("sessionUrl")));
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(Map.of("error", "Invalid response from payment service."));
				}
			} else {
				return ResponseEntity.badRequest().body(Map.of("error", "Payment processing failed."));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Internal error: " + e.getMessage()));
		}
	}

	// Simulated method to fetch booking details (Replace this with actual DAO call)
	private Map<String, Object> getBookingDetails(int serviceId, int bookingId) {
		// Mocked response (replace with DB call)
		return Map.of("username", "testUser", "password", "testPass");
	}
}
