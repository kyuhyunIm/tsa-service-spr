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

        String cp = isWindows
            ? ".;JUSToolkit-1.2.13.0.jar"
            : ".:JUSToolkit-1.2.13.0.jar";

        String command = String.format("cd lib && javac -encoding UTF8 -cp %s GetTSAToken.java", cp);
        String[] execCommand = isWindows
            ? new String[]{"cmd", "/c", command}
            : new String[]{"bash", "-c", command};

        runCommand(execCommand);
    }

    public String run(String hash) throws IOException, InterruptedException {
        String command = buildCommand(hash);
        String os = System.getProperty("os.name").toLowerCase();
        String[] execCommand = os.contains("win")
            ? new String[]{"cmd", "/c", command}
            : new String[]{"bash", "-c", command};

        return runCommand(execCommand);
    }

    private String buildCommand(String hash) {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean isMac = os.contains("mac");

        String cpSeparator = isWindows ? ";" : ":";
        String cpOption = "-cp ."+cpSeparator+"JUSToolkit-1.2.13.0.jar";
        String libPathOption = "-Djava.library.path=.";
        String envPrefix = "";

        if (!isWindows) {
            envPrefix = isMac
                ? "DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:$PWD"
                : "LD_LIBRARY_PATH=$PWD";
        }

        String tsaArgs = isLocal()
            ? "--key-path=../cert/license.dev.key --cert-path=../cert/signCert.der --tsa-server=dev"
            : "--tsa-server=prod";

        String baseCommand = isWindows
            ? String.format("cd lib && java %s %s GetTSAToken \"%s\" %s", libPathOption, cpOption, hash, tsaArgs)
            : String.format("cd lib && %s java %s %s GetTSAToken \"%s\" %s", envPrefix, libPathOption, cpOption, hash, tsaArgs);

        return baseCommand;
    }

    private String runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process failed with exit code " + exitCode + "\n" + output);
        }

        return output.toString().trim();
    }

    private boolean isLocal() {
        // 환경변수 또는 다른 설정으로 변경 가능
        return true;
    }
}
