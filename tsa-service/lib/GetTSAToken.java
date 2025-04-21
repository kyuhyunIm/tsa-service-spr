import java.io.File;
import java.io.FileReader;

import com.crosscert.justoolkit;

public class GetTSAToken {

	public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java GetTSAToken <HashString>");
            System.exit(1);
        }
        String keyPath = "/tmp/cert/license.key";
        String certPath = "/tmp/cert/signCert.der";
        String hashString = null;

        String tsa_server = "";

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--key-path=")) {
                keyPath = args[i].substring("--key-path=".length());
            } else if (args[i].startsWith("--cert-path=")) {
                certPath = args[i].substring("--cert-path=".length());
            } else if (hashString == null) {
                hashString = args[i];
            } else if (args[i].startsWith("--tsa-server=")) {
				// TSA토큰 생성 운영IP : 211.192.169.142 / 개발IP: 211.180.234.201
                String server = args[i].substring("--tsa-server=".length());
                if ( server.startsWith("dev") ) {
                    tsa_server = "211.180.234.201";
                }
                else if ( server.startsWith("prod") ) {
                    tsa_server = "211.192.169.142";
                }
                else {
                    System.err.println("Invalid TSA server argument. Use 'dev' or 'prod'.");
                    System.exit(1);
                }
            } else if (args[i].startsWith("--help")) {
                System.out.println("Usage: java GetTSAToken <HashString> [--key-path=<path>] [--cert-path=<path>] [--tsa-server=<server>]");
                System.exit(0);
            }
            else {
                System.err.println("Unexpected argument: " + args[i]);
                System.exit(1);
            }
        }

        if (hashString == null) {
            System.err.println("Usage: java GetTSAToken <HashString>");
            System.exit(1);
        }

		justoolkit oJustoolkit = new justoolkit();

		try {
			String USToolkitLicense = "";
			try {
				// USToolkit 라이센스 읽어온다.
				File file = new File(keyPath); // 라이센스 key 파일 경로
				FileReader filereader = new FileReader(file);
				int singleCh = 0;
				while ((singleCh = filereader.read()) != -1) {
					USToolkitLicense += (char) singleCh;
				}
				filereader.close();
			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}
			oJustoolkit.init(USToolkitLicense);// USToolkit 라이센스 init
			// TSA 용도의 인증서를 읽어온다. 경로 셋팅
			byte[] signCert = oJustoolkit.UTIL_ReadFile(certPath);
			// TSA Input HashString
			String hexString = hashString;
			// TSA 용 Hash 데이터
			// byte[] hashValue = oJustoolkit
			// .UTIL_HexStringToBin("994a0ec597c822ee046b1226c43547a832b581848ecc97d7d4dec187f93609ab");
			byte[] hashValue = oJustoolkit.UTIL_HexStringToBin(hexString);
// 			System.out.println("Hash value length: " + hashValue.length);
// 			System.out.println("Sign cert length: " + signCert.length);

			try {
				byte[] baTSAToken = oJustoolkit.TSA_RequestTimeStampWithHash( tsa_server, 4299, signCert, hashValue);
// 				System.out.println("TSA Token length: " + (baTSAToken != null ? baTSAToken.length : "null"));

				if (baTSAToken == null) {
					System.err.println("TSA server returned null token. Check server status and certificate validity.");
					System.exit(1);
				}

				boolean bRet = oJustoolkit.TSA_VerifyTimeStampTokenWithHash(hashValue, baTSAToken);

				System.out.println(oJustoolkit.UTIL_Base64Encode(baTSAToken));
				if (!bRet) {
					System.err.println("Validation vailed");
					System.exit(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			oJustoolkit.finish();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
