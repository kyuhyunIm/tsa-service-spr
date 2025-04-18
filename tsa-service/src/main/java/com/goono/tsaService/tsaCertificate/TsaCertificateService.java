package com.goono.tsaService.tsaCertificate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TsaCertificateService {
	private final TsaCertificateRepository tsaCertificateRepository;
	
	public void create(String hash) {
		TsaCertificate tsaCertificate = new TsaCertificate();
		tsaCertificate.setHash(hash);
		tsaCertificate.setPolicy("mock policy");
		tsaCertificate.setAlgorithm("mock algorithm");
		tsaCertificate.setSerialNumber("mock serialNumber");
		tsaCertificate.setIssuer("mock issuer");
		tsaCertificate.setNonce("mock nonce");
		tsaCertificate.setTimestamp("mock timestamp");
		this.tsaCertificateRepository.save(tsaCertificate);
	}
}
