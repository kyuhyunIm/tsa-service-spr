package com.goono.tsaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TsaServiceController {
	@GetMapping("/")
	@ResponseBody
	public String root() {
		return "tsa-service is running";
	}
}
