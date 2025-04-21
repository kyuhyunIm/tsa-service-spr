package com.goono.tsaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.goono.tsaService.tsaCertificate.TsaRunner;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TsaServiceController {
	private final TsaRunner tsaRunner;
	
	@GetMapping("/")
	public String root() {
		return "tsa_certificate_list";
	}
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		try {
			tsaRunner.ready();
			String result = tsaRunner.run("test_hash");
			return result;
		} catch(Exception e){
			e.printStackTrace();
			return "error";
		}
	}
}
