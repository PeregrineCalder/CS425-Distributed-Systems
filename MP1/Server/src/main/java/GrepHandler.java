import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @className: GrepHandler
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */

public class GrepHandler {
    public String grep(String command) {
        StringBuilder result = new StringBuilder();
        int lineCount = 0;
        try {
            String[] commandDetails = command.split(" ");
            if (isRegexPattern(command) && !containsExtendedOption(commandDetails)) {
                commandDetails = addExtendedOption(commandDetails);
            }
            ProcessBuilder processBuilder = new ProcessBuilder(commandDetails);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
                lineCount++;
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                result.append("Grep command failed with exit code: ").append(exitCode).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Matched lines: " + lineCount + "\n" + result.toString();
    }

    private boolean isRegexPattern (String command) {
        return command.contains("*") || command.contains("+") || command.contains("?") ||
                command.contains("[") || command.contains("]") || command.contains("{") ||
                command.contains("}") || command.contains("^") || command.contains("$") ||
                command.contains("(") || command.contains(")") || command.contains(".") ||
                command.contains("|");
    }

    private boolean containsExtendedOption (String[] commandDetails) {
        for (String part : commandDetails) {
            if (part.equals("-E")) {
                return true;
            }
        }
        return false;
    }

    // Create a new array and add "-E" after "grep"
    private String[] addExtendedOption(String[] commandDetails) {
        String[] newCommandDetails = new String[commandDetails.length + 1];
        newCommandDetails[0] = commandDetails[0];  // grep
        newCommandDetails[1] = "-E";  // 插入 -E 选项
        System.arraycopy(commandDetails, 1, newCommandDetails, 2, commandDetails.length - 1);
        return newCommandDetails;
    }
}
