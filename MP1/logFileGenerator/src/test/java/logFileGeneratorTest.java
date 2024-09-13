/**
 * @projectName: CS425-Distributed-Systems
 * @className: logFileGeneratorTest
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class logFileGeneratorTest {
    @Test
    public void test_GenerateOutputFile() {
        try {
            String machineID = "1";
            String[] args = {machineID};
            logFileGenerator.main(args);
            Path logFilePath = Paths.get("./logFile/machine." + machineID + ".log");
            System.out.println("Checking if file exists: " + logFilePath.toString());
            Assertions.assertTrue(Files.exists(logFilePath), "Log file should be generated.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
