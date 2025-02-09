<!-- workerDashboard.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Worker Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .booking-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .booking-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .booking-id {
            font-size: 1.2em;
            font-weight: bold;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 0.9em;
        }

        .status-6 { /* BOOKED */
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .status-7 { /* IN_PROGRESS */
            background-color: #fff3e0;
            color: #f57c00;
        }

        .status-9 { /* COMPLETED */
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .booking-details {
            margin-bottom: 15px;
        }

        .booking-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        button {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        button.start {
            background-color: #2196f3;
            color: white;
        }

        button.complete {
            background-color: #4caf50;
            color: white;
        }

        button:hover {
            opacity: 0.9;
        }

        button:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
        }

        .error-message {
            color: #d32f2f;
            margin-top: 10px;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Worker Dashboard</h1>
        
        <div id="bookings-container">
            <!-- Bookings will be loaded here dynamically -->
        </div>
    </div>

    <script>
        // Configuration
        const SPRING_API_URL = 'http://localhost:8080/api'; // Adjust to your Spring server URL
        const WORKER_ID = '1'; // Hardcoded for now, should come from login later

        // Fetch bookings from Spring server
        async function fetchBookings() {
            try {
                const response = await fetch(`${SPRING_API_URL}/bookings?workerId=${WORKER_ID}`);
                if (!response.ok) throw new Error('Failed to fetch bookings');
                const bookings = await response.json();
                displayBookings(bookings);
            } catch (error) {
                console.error('Error:', error);
                showError('Failed to load bookings');
            }
        }

        // Update booking status
        async function updateStatus(bookingId) {
            try {
                const response = await fetch(`${SPRING_API_URL}/bookings/${bookingId}/status`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Worker-ID': WORKER_ID
                    }
                });

                if (!response.ok) throw new Error('Failed to update status');
                
                // Refresh bookings after successful update
                fetchBookings();
            } catch (error) {
                console.error('Error:', error);
                showError('Failed to update booking status');
            }
        }

        // Display bookings in the UI
        function displayBookings(bookings) {
            const container = document.getElementById('bookings-container');
            
            if (!bookings || bookings.length === 0) {
                container.innerHTML = '<p>No bookings available.</p>';
                return;
            }

            const bookingsHtml = bookings.map(booking => `
                <div class="booking-card">
                    <div class="booking-header">
                        <span class="booking-id">Booking #${booking.bookingId}</span>
                        <span class="status-badge status-${booking.statusId}">
                            ${getStatusLabel(booking.statusId)}
                        </span>
                    </div>
                    <div class="booking-details">
                        <p>Service: ${booking.serviceName}</p>
                        <p>Schedule: ${new Date(booking.scheduledTime).toLocaleString()}</p>
                    </div>
                    <div class="booking-actions">
                        ${getActionButton(booking.statusId, booking.bookingId)}
                    </div>
                </div>
            `).join('');

            container.innerHTML = bookingsHtml;
        }

        // Get status label based on status ID
        function getStatusLabel(statusId) {
            switch (statusId) {
                case 6: return 'BOOKED';
                case 7: return 'IN PROGRESS';
                case 9: return 'COMPLETED';
                default: return 'UNKNOWN';
            }
        }

        // Get appropriate action button based on status
        function getActionButton(statusId, bookingId) {
            switch (statusId) {
                case 6:
                    return `<button onclick="updateStatus(${bookingId})" class="start">
                        Start Service
                    </button>`;
                case 7:
                    return `<button onclick="updateStatus(${bookingId})" class="complete">
                        Complete Service
                    </button>`;
                default:
                    return '';
            }
        }

        // Show error message
        function showError(message) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.textContent = message;
            document.querySelector('.container').insertBefore(
                errorDiv, 
                document.getElementById('bookings-container')
            );
            setTimeout(() => errorDiv.remove(), 5000);
        }

        // Initialize and set up auto-refresh
        document.addEventListener('DOMContentLoaded', () => {
            fetchBookings();
            setInterval(fetchBookings, 30000); // Refresh every 30 seconds
        });
    </script>
</body>
</html>