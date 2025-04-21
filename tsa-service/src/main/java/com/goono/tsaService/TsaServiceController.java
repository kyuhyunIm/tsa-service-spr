package com.goono.tsaService;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.goono.tsaService.tsaCertificate.TsaCertificate;
import com.goono.tsaService.tsaCertificate.TsaCertificateService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TsaServiceController {
	private final TsaCertificateService tsaCertificateService;

	@GetMapping("/")
	public String root(Model model) {
		List<TsaCertificate> tsaCertificateList = tsaCertificateService.getList();
		model.addAttribute(tsaCertificateList);
		return "tsa_certificate_form";
	}
}
