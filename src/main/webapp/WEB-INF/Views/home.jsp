<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Checkout Session</title>
<script>
    function submitForm(event) {
        event.preventDefault(); // Prevent default form submission

        const formData = {
            booking_id: document.getElementById("booking_id").value,
            amount: document.getElementById("amount").value,
            quantity: 1,  // Defaulted
            currency: "SGD" // Defaulted
        };

        fetch('/api/payments/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl; // Redirect to payment page
            } else {
                document.getElementById("response").innerText = "Error: " + data.error;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById("response").innerText = "An error occurred.";
        });
    }
</script>
</head>
<body>

	<h2>Enter Booking Details</h2>
	<form onsubmit="submitForm(event)">
		<label for="booking_id">Booking ID:</label> <input type="number"
			id="booking_id" name="booking_id" required><br> <br>

		<label for="amount">Amount:</label> <input type="number" id="amount"
			name="amount" required><br> <br>

		<button type="submit">Proceed to Checkout</button>
	</form>

	<p id="response" style="color: red;"></p>

</body>
</html>
