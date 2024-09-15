import lombok.Getter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: GrepHandler
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */

@Getter
public class GrepHandler {
    private int exitCode;
    List<String> options = new ArrayList<>();
    public byte[] grep(String command) {
        String[] commandDetails = command.split(" ");
        for (int i = 1; i < commandDetails.length; i++) {
            String part = commandDetails[i];
            if (part.startsWith("-")) {
                for (int j = 1; j < part.length(); j++) {
                    options.add(String.valueOf(part.charAt(j)));
                }
            } else if ((i == 1 || commandDetails[i - 1].startsWith("-"))
                        && part.startsWith("\"")
                        && part.endsWith("\"")
                        && part.length() > 1) {
                commandDetails[i] = part.substring(1, part.length() - 1);
            } else if (part.startsWith("\"") && (i == 1 || commandDetails[i - 1].startsWith("-"))) {
                commandDetails[i] = part.substring(1);
            } else if (part.endsWith("\"") && (i == commandDetails.length - 2 || commandDetails[i + 1].startsWith("-"))) {
                commandDetails[i] = part.substring(0, part.length() - 1);
            }
        }
        return executeGrep(commandDetails);
    }

    private byte[] executeGrep(String[] commandDetails) {
        StringBuilder result = new StringBuilder();
        int lineCount = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commandDetails);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (options.contains("c")) {
                    try {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            lineCount += Integer.parseInt(parts[1].trim());
                        } else {
                            lineCount += Integer.parseInt(parts[0].trim());
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line count: " + line);
                    }
                } else {
                    result.append(line).append("\n");
                    if (!options.contains("l") && !options.contains("L")) {
                        lineCount++;
                    }
                }
            }
            exitCode = process.waitFor();
            if (exitCode != 0) {
                result.append("Grep command failed with exit code: ").append(exitCode).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (options.contains("c")) {
            return ("Matched lines: " + lineCount).getBytes(StandardCharsets.UTF_8);
        } else if (options.contains("l") || options.contains("L")) {
            if (result.isEmpty()) {
                return "No files found matching the criteria.\n".getBytes(StandardCharsets.UTF_8);
            } else {
                return result.toString().getBytes(StandardCharsets.UTF_8);
            }
        } else if (options.contains("q")) {
            return new byte[0];
        } else {
            String finalResult = "Matched lines: " + lineCount + "\n" + result;
            return finalResult.getBytes(StandardCharsets.UTF_8);
        }
    }
}
