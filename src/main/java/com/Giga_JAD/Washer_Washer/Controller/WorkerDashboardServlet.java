package com.Giga_JAD.Washer_Washer.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;

import com.Giga_JAD.Washer_Washer.Class.*;

//@WebServlet(
//    name = "WasherController",
//    urlPatterns = {"/washer/*", "/worker/*"},
//    loadOnStartup = 1
//)

@WebServlet("/WorkerDashboard")
public class WorkerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_BASE_URL = "http://localhost:8080/api"; // Your Spring server URL

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Hardcoded worker ID for now - should come from session after login
        String workerId = "1";
        
        Client client = ClientBuilder.newClient();
        String restUrl = API_BASE_URL + "/bookings?workerId=" + workerId;
        WebTarget target = client.target(restUrl);
        
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response resp = invocationBuilder.get();
        
        System.out.println("Status: " + resp.getStatus());
        
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Successfully fetched bookings");
            ArrayList<Booking> bookings = resp.readEntity(new GenericType<ArrayList<Booking>>() {});
            request.setAttribute("bookings", bookings);
            RequestDispatcher rd = request.getRequestDispatcher("/workerDashboard.jsp");
            rd.forward(request, response);
        } else {
            System.out.println("Failed to fetch bookings. Status: " + resp.getStatus());
            request.setAttribute("error", "Failed to fetch bookings");
            RequestDispatcher rd = request.getRequestDispatcher("/workerDashboard.jsp");
            rd.forward(request, response);
        }
    }

    // Handle status update POST requests
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String bookingId = request.getParameter("bookingId");
        String workerId = "1"; // Hardcoded for now
        
        if (bookingId == null || bookingId.trim().isEmpty()) {
            request.setAttribute("error", "Invalid booking ID");
            doGet(request, response);
            return;
        }

        Client client = ClientBuilder.newClient();
        String restUrl = API_BASE_URL + "/bookings/" + bookingId + "/status";
        WebTarget target = client.target(restUrl);
        
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON)
                                                   .header("Worker-ID", workerId);
        
        Response resp = invocationBuilder.put(null); // null because no body needed
        
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Successfully updated booking status");
            response.sendRedirect(request.getContextPath() + "/WorkerDashboard");
        } else {
            System.out.println("Failed to update status. Status: " + resp.getStatus());
            request.setAttribute("error", "Failed to update booking status");
            doGet(request, response);
        }
    }
}