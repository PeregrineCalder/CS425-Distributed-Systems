import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
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
        this.grepHandler = new GrepHandler();
        this.logFilePath = getClass().getClassLoader().getResource("Test.log").getPath();
        this.pattern = "\\d{4}-\\d{2}-\\d{2}";
        System.out.println("Finish Configuration");
    }

    @Test
    public void test_GrepCommand() {
        System.out.println(logFilePath);
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
        System.out.println(logFilePath);
        String command = "grep -E " + pattern + " " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }

    @Test
    public void test_GrepCommandRegexWithoutOption() {
        System.out.println(logFilePath);
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        String command = "grep " + pattern + " " + logFilePath;
        String result = grepHandler.grep(command);
        System.out.println(result);
    }
}
