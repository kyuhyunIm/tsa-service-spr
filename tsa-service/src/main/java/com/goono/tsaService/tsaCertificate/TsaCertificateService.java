package com.goono.tsaService.tsaCertificate;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TsaCertificateService {
	private final TsaRunner tsaRunner;
	private final TsaCertificateRepository tsaCertificateRepository;
	private final TsaTokenParser tsaTokenParser;

	public void create(String hash) throws Exception {
		tsaRunner.ready();
		String tsaToken = tsaRunner.run(hash);
		TsaCertificate result = tsaTokenParser.parse(tsaToken);
		tsaCertificateRepository.save(result);
	}

	public List<TsaCertificate> getList() {
		return tsaCertificateRepository.findAll();
	}
}
