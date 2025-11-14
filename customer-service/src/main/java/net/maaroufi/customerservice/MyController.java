package net.maaroufi.customerservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

	
	
	@GetMapping("api/vai")
	String vai() {
		
		return "home";
	}
}
