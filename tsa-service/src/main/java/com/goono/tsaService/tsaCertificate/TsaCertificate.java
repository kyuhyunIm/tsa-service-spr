package com.goono.tsaService.tsaCertificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TsaCertificate {
	@Id
	@Column(nullable = false)
	private String hash;
	
	@Column(nullable = false)
	private String policy;
	
	@Column(nullable = false)
	private String algorithm;
	
	@Column(nullable = false)
	private String serialNumber;
	
	@Column(nullable = false)
	private String issuer;
	
	@Column(nullable = false)
	private String nonce;
	
	@Column(nullable = false)
	private String timestamp;
}
