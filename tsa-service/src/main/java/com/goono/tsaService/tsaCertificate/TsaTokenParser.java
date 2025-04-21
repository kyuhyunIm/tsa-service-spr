package com.goono.tsaService.tsaCertificate;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TsaTokenParser {
	public TsaCertificate parse(String inputBase64) throws Exception {
		byte[] decoded = Base64.decode(inputBase64);
		ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(decoded));
		ASN1Sequence root = (ASN1Sequence) asn1InputStream.readObject();

		// 이 부분은 TypeScript의 obj.sub[1]... 구조를 그대로 따라감
		ASN1Sequence contentInfo = (ASN1Sequence) root.getObjectAt(1);
		ASN1Sequence signedData = (ASN1Sequence) contentInfo.getObjectAt(0);
		ASN1Sequence tsInfo = (ASN1Sequence) ((ASN1TaggedObject) ((ASN1Sequence) signedData.getObjectAt(2))
				.getObjectAt(1)).getObject();

		// 해시 알고리즘
		ASN1ObjectIdentifier hashAlgOID = (ASN1ObjectIdentifier) ((ASN1Sequence) ((ASN1Sequence) tsInfo.getObjectAt(2))
				.getObjectAt(0)).getObjectAt(0);
		String hashAlg = getDigestAlgorithmName(hashAlgOID.getId());

		// 메시지 다이제스트
		ASN1OctetString messageImprintOctets = (ASN1OctetString) ((ASN1Sequence) tsInfo.getObjectAt(2)).getObjectAt(1);
		String messageHash = bytesToHex(messageImprintOctets.getOctets());

		String policy = ((ASN1ObjectIdentifier) tsInfo.getObjectAt(1)).getId();
		BigInteger serial = ((ASN1Integer) tsInfo.getObjectAt(3)).getValue();
		String genTime = tsInfo.getObjectAt(4).toString();
		BigInteger nonce = ((ASN1Integer) tsInfo.getObjectAt(5)).getValue();

		// 서명 관련 데이터
		ASN1Sequence signerInfo = (ASN1Sequence) ((ASN1Set) signedData.getObjectAt(4)).getObjectAt(0);
		ASN1Sequence digestAlgorithm = (ASN1Sequence) signerInfo.getObjectAt(2);
		String authHashAlg = getDigestAlgorithmName(((ASN1ObjectIdentifier) digestAlgorithm.getObjectAt(0)).getId());

		ASN1OctetString signedAttr = (ASN1OctetString) ((ASN1TaggedObject) ((ASN1Sequence) signerInfo.getObjectAt(3))
				.getObjectAt(2)).getObject();

		// 서명
		DERBitString signatureBitString = (DERBitString) signerInfo.getObjectAt(5);
		BigInteger signature = new BigInteger(1, signatureBitString.getBytes());

		// 인증서에서 공개키 추출
		ASN1Sequence certificates = (ASN1Sequence) ((ASN1TaggedObject) signedData.getObjectAt(3)).getObject();
		ASN1Sequence certSeq = (ASN1Sequence) certificates.getObjectAt(0);
		SubjectPublicKeyInfo pubKeyInfo = SubjectPublicKeyInfo.getInstance(certSeq.getObjectAt(6));

		ASN1Sequence rsaPubKey = (ASN1Sequence) ASN1Primitive.fromByteArray(pubKeyInfo.getPublicKeyData().getBytes());
		BigInteger pubMod = ((ASN1Integer) rsaPubKey.getObjectAt(0)).getValue();
		BigInteger pubExp = ((ASN1Integer) rsaPubKey.getObjectAt(1)).getValue();

		// RSA 복호화 (modPow)
		BigInteger decrypted = signature.modPow(pubExp, pubMod);
		String decryptedHex = decrypted.toString(16);
		String hashFromSignature = extractHashFromDecrypted(decryptedHex);

		// 검증
		MessageDigest digest = MessageDigest.getInstance(authHashAlg);
		byte[] authDigest = digest.digest(signedAttr.getEncoded());
		String calcAuthHash = bytesToHex(authDigest);

		if (!hashFromSignature.equalsIgnoreCase(calcAuthHash)) {
			throw new Exception("RSA 복호화된 해시값이 검증 실패함");
		}

		// 결과 반환
		TsaCertificate cert = new TsaCertificate();
		cert.setPolicy(policy);
		cert.setAlgorithm(hashAlg);
		cert.setHash(messageHash);
		cert.setSerialNumber(serial.toString());
		cert.setTimestamp(genTime);
		cert.setIssuer("..."); // issuer 파싱 시 구현 필요
		cert.setNonce(nonce.toString());
		return cert;
	}

	private static String extractHashFromDecrypted(String decryptedHex) throws IOException {
		// 서명은 PKCS#1 v1.5 형식: padding 후 ASN.1 DigestInfo 포함
		Pattern pattern = Pattern.compile("(?:0{0,1}1)(ff)*00([a-f0-9]+)$");
		Matcher matcher = pattern.matcher(decryptedHex);
		if (!matcher.find())
			throw new IOException("패딩 제거 실패");

		String digestPart = matcher.group(2);
		byte[] digestBytes = hexStringToByteArray(digestPart);
		ASN1Sequence digestSeq = (ASN1Sequence) ASN1Primitive.fromByteArray(digestBytes);
		ASN1OctetString digest = (ASN1OctetString) digestSeq.getObjectAt(1);
		return bytesToHex(digest.getOctets());
	}

	private static String getDigestAlgorithmName(String oid) {
		switch (oid) {
		case "2.16.840.1.101.3.4.2.1":
			return "SHA-256";
		case "1.3.14.3.2.26":
			return "SHA-1";
		case "2.16.840.1.101.3.4.2.3":
			return "SHA-512";
		default:
			return "SHA-256"; // 기본값
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		return data;
	}
}
