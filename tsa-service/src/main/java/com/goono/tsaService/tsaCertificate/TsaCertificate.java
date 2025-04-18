package com.goono.tsaService.tsaCertificate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TsaCertificate {
	@Id
	private String hash;
	
	private String policy;
	
	private String algorithm;
	
	private String serialNumber;
	
	private String issuer;
	
	private String nonce;
	
	private String timestamp;
}
