package com.goono.tsaService.tsaCertificate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequestMapping("/tsaCertificate")
@RequiredArgsConstructor
@RestController
public class TsaCertificateController {
	private final TsaCertificateService tsaCertificateService;

	@PostMapping("/create/{hash}")
	public ResponseEntity<String> create(@PathVariable("hash") String hash) {
		this.tsaCertificateService.create(hash);
		return ResponseEntity.status(HttpStatus.CREATED).body("created successfully");
	}
}
