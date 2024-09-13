import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * @className: GrepHandlerTest
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */
public class GrepHandlerTest {
    GrepHandler grepHandler;
    String logFilePath;
    String pattern;

    @BeforeEach
    public void setup() {
        grepHandler = new GrepHandler();
        logFilePath = Paths.get(URLDecoder.decode(getClass().getClassLoader().getResource("Test.log").getPath(), StandardCharsets.UTF_8)).toString();
        pattern = "\\d{4}-\\d{2}-\\d{2}";
    }

    @Test
    public void test_GrepCommand() {
        String command = "grep ERROR " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }

    @Test
    public void test_GrepCommandNoMatch() {
        String command = "grep NOTHING " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }

    @Test
    public void test_GrepCommandRegexWithOption() {
        String command = "grep -E " + pattern + " " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }

    @Test
    public void test_GrepCommandRegexWithoutOption() {
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        String command = "grep " + pattern + " " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }
}
