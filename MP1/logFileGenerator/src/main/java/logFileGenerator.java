/**
 * @projectName: CS425-Distributed-Systems
 * @className: logFileGenerator
 * @author: Peregrine Calder
 * @description: To generate log file.
 * @version: 1.0
 */
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
public class logFileGenerator {
    private static final int MAX_LOG_FILE_LINES = 500;
    @Getter
    private static final int MAX_NUMBER_OF_WORDS_PER_LINE = 50;
    private static final int MAX_LOG_FILE_LEVEL = 6;
    @Getter
    private static final int MAX_WORD_LENGTH = 15;
    private static final String[] logLevels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
    private static final String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private static final Logger logger = LogManager.getLogger(logFileGenerator.class);
    private static final Random random = new Random();


    public static String generateLogFileLine() {
        StringBuilder logFileLine = new StringBuilder();
        int numberOfWordsPerLine = random.nextInt(MAX_NUMBER_OF_WORDS_PER_LINE) + 1;
        for (int i = 0; i < numberOfWordsPerLine; i++) {
            int wordLength = random.nextInt(MAX_WORD_LENGTH) + 1;
            StringBuilder word = new StringBuilder(wordLength);
            for (int j = 0; j < wordLength; j++) {
                word.append(charSet.charAt(random.nextInt(charSet.length())));
            }
            logFileLine.append(word).append(" ");
        }
        return logFileLine.toString().trim();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Machine ID is required.");
            return;
        }

        int numberOfLogFileLines = random.nextInt(MAX_LOG_FILE_LINES);
        for (int i = 0; i < numberOfLogFileLines; i++) {
            String message = generateLogFileLine();
            logger.log(Level.valueOf(logLevels[random.nextInt(MAX_LOG_FILE_LEVEL)]), message);
        }

        String machineID = args[0];
        String logFileName = "./logFile/machine." + machineID + ".log";
        Path logPath = Paths.get(logFileName);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(date);
        String originalLogFileName = "./logFile/machine." + dateString + ".log";
        Path originalLogPath = Paths.get(originalLogFileName);

        Files.copy(originalLogPath, logPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
