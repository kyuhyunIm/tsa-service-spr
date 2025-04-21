package com.goono.tsaService.tsaCertificate;

import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class TsaRunner {

    public void ready() throws IOException, InterruptedException {
        compile();
    }

    public void clean() {
        new File("lib/GetTSAToken.class").delete();
    }

    public void compile() throws IOException, InterruptedException {
    	String os = System.getProperty("os.name").toLowerCase();
    	boolean isWindows = os.contains("win");

    	String compileCmd = isWindows
    	        ? "cd lib && javac -encoding UTF8 -cp .;JUSToolkit-1.2.13.0.jar GetTSAToken.java"
    	        : "cd lib && javac -encoding UTF8 -cp .:JUSToolkit-1.2.13.0.jar GetTSAToken.java";

    	String[] command = isWindows
    	        ? new String[]{"cmd", "/c", compileCmd}
    	        : new String[]{"bash", "-c", compileCmd};
    	
        runCommand(command);
    }

    public String run(String hash) throws IOException, InterruptedException {
        boolean isLocal = isLocal();
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        System.out.println(isWindows);

        String cpSeparator = isWindows ? ";" : ":";

        String command;
        if (isLocal) {
            if (isWindows) {
                command = String.format(
                    "cd lib && java -cp .%sJUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --key-path=../cert/license.dev.key --cert-path=../cert/signCert.der --tsa-server=dev",
                    cpSeparator,
                    hash
                );
            } else {
                command = String.format(
                    "cd lib && LD_LIBRARY_PATH=$PWD java -cp .%sJUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --key-path=../cert/license.dev.key --cert-path=../cert/signCert.der --tsa-server=dev",
                    cpSeparator,
                    hash
                );
            }
        } else {
            if (isWindows) {
                command = String.format(
                    "cd lib && java -cp .%sJUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --tsa-server=prod",
                    cpSeparator,
                    hash
                );
            } else {
                command = String.format(
                    "cd lib && LD_LIBRARY_PATH=$PWD java -cp .%sJUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --tsa-server=prod",
                    cpSeparator,
                    hash
                );
            }
        }

        String[] execCommand = isWindows
                ? new String[]{"cmd", "/c", command}
                : new String[]{"bash", "-c", command};

        return runCommand(execCommand);
    }

    private String runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process failed with exit code " + exitCode + "\n" + output);
        }

        return output.toString().trim();
    }

    private boolean isLocal() {
        return true;
    }
}
