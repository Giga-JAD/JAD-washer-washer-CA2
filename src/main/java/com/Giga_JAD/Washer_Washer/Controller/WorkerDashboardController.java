package com.Giga_JAD.Washer_Washer.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Giga_JAD.Washer_Washer.Service.WorkerService;
import com.Giga_JAD.Washer_Washer.model.Booking;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/worker")
public class WorkerDashboardController {

	private final WorkerService workerService;

	public WorkerDashboardController(WorkerService workerService) {
		this.workerService = workerService;
	}

	// Fetch bookings assigned to a worker
	@GetMapping("/dashboard")
	public ResponseEntity<?> getWorkerBookings(HttpSession session) {
		// Retrieve workerId from session (update authentication logic as needed)
		String workerId = (String) session.getAttribute("workerId");
		if (workerId == null) {
			return ResponseEntity.status(401).body("Unauthorized: Worker ID missing in session");
		}

		List<Booking> bookings = workerService.getBookingsForWorker(workerId);
		return ResponseEntity.ok(bookings);
	}

	// Update booking status
	@PutMapping("/bookings/{bookingId}/status")
	public ResponseEntity<?> updateBookingStatus(@PathVariable String bookingId, HttpSession session) {
		String workerId = (String) session.getAttribute("workerId");

		if (workerId == null) {
			return ResponseEntity.status(401).body("Unauthorized: Worker ID missing in session");
		}

		boolean success = workerService.updateBookingStatus(bookingId, workerId);
		if (success) {
			return ResponseEntity.ok("Booking status updated successfully");
		} else {
			return ResponseEntity.status(400).body("Failed to update booking status");
		}
	}
}
