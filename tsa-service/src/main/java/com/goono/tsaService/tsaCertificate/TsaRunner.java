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
        String[] command = {
                "bash",
                "-c",
                "cd lib && javac -encoding UTF8 -cp .:JUSToolkit-1.2.13.0.jar GetTSAToken.java"
        };
        runCommand(command);
    }

    public String run(String hash) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isLocal = isLocal();

        String command;
        if (isLocal) {
            if (os.contains("mac")) {
                command = String.format(
                        "cd lib && DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:$PWD java -cp .:JUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --key-path=../cert/license.key --cert-path=../cert/signCert.der --tsa-server=dev",
                        hash
                );
            } else {
                command = String.format(
                        "cd lib && LD_LIBRARY_PATH=$PWD java -cp .:JUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --key-path=../cert/license.dev.key --cert-path=../cert/signCert.der --tsa-server=dev",
                        hash
                );
            }
        } else {
            command = String.format(
                    "cd lib && LD_LIBRARY_PATH=$PWD java -cp .:JUSToolkit-1.2.13.0.jar GetTSAToken \"%s\" --tsa-server=prod",
                    hash
            );
        }

        return runCommand(new String[]{"bash", "-c", command});
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
        String env = System.getenv("SPRING_PROFILES_ACTIVE");
        return "local".equalsIgnoreCase(env);
    }
}
