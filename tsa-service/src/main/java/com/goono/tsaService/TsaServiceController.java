package com.goono.tsaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TsaServiceController {
	@GetMapping("/")
	public String root() {
		return "tsa_certificate_list";
	}
}
