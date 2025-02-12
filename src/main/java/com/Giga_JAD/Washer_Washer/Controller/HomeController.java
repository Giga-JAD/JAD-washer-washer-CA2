package com.Giga_JAD.Washer_Washer.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String showHomePage() {
		return "home"; // This maps to /WEB-INF/views/home.jsp
	}
}
