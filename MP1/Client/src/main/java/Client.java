import org.springframework.util.StopWatch;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * @className: Client
 * @author: Peregrine Calder
 * @description:
 * @version: 1.0
 */
public class Client {
    private static final String virtualMachineNetworkConfigPath = "./src/resources/virtualMachineNetworkConfig.properties";
    public static void main(String[] args) {
        // Get grep command
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        // Timer
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try (InputStream inputStream = new FileInputStream(virtualMachineNetworkConfigPath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] hostNames = properties.getProperty("hostnames").split(",");
            String[] ports = properties.getProperty("ports").split(",");
            int numberOfVMs = hostNames.length;
            ClientProcessor[] clientProcessors = new ClientProcessor[numberOfVMs];
            Thread[] threads = new Thread[numberOfVMs];
            for (int i = 0; i < numberOfVMs; i++) {
                String dstServerAddress = hostNames[i] + "::" + ports[i];
                clientProcessors[i] = ClientProcessor.builder()
                        .hostname(hostNames[i])
                        .port(Integer.parseInt(ports[i]))
                        .dstServerAddress(dstServerAddress)
                        .command(command)
                        .build();
                threads[i] = new Thread(clientProcessors[i]);
                threads[i].start();
            }
            for (int i = 0; i < numberOfVMs; i++) {
                threads[i].join();
            }
            System.out.println("Total matched files: " + ClientProcessor.getGrepFileCount());
            System.out.println("Total matched lines: " + ClientProcessor.getGrepTotalLineCount());
            for (String result : ClientProcessor.getAllGrepResults()) {
                System.out.println(result);
            }
        } catch (IOException e) {
            System.out.println("Error reading network configuration file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        stopWatch.stop();
        System.out.println("Execution time: " + stopWatch.prettyPrint());
    }
}
