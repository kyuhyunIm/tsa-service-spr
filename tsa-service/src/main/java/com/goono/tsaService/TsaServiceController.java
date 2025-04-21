package com.goono.tsaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.goono.tsaService.tsaCertificate.TsaRunner;

import lombok.RequiredArgsConstructor;

@Controller
public class TsaServiceController {
	@GetMapping("/")
	public String root() {
		return "tsa_certificate_form";
	}
}
