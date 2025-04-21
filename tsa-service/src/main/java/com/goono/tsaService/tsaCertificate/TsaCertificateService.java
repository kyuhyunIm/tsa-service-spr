package com.goono.tsaService.tsaCertificate;

import java.io.IOException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TsaCertificateService {
	private final TsaRunner tsaRunner;
	private final TsaCertificateRepository tsaCertificateRepository;
	
	public void create(String hash) throws IOException, InterruptedException {
		tsaRunner.ready();
		String tsaToken = tsaRunner.run(hash);
		
		// TODO::tsa_service.generateToken
	}
}
