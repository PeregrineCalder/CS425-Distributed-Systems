import org.springframework.util.StopWatch;
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
    private static final String virtualMachineNetworkConfigPath = "virtualMachineNetworkConfig.properties";
    public static void main(String[] args) {
        try (InputStream inputStream = Client.class.getClassLoader().getResourceAsStream(virtualMachineNetworkConfigPath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] hostNames = properties.getProperty("hostnames").split(",");
            String[] ports = properties.getProperty("ports").split(",");
            String[] filePaths = properties.getProperty("filepaths").split(",");
            int numberOfVMs = hostNames.length;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter grep command (or type 'exit' to quit): ");
                String command = scanner.nextLine();

                // Exit the loop if the user types 'exit'
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the client.");
                    break;
                }

                // Timer
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                // Create and run client processor
                ClientProcessor[] clientProcessors = new ClientProcessor[numberOfVMs];
                Thread[] threads = new Thread[numberOfVMs];
                for (int i = 0; i < numberOfVMs; i++) {
                    String grepCommand = command + " " + filePaths[i];
                    String dstServerAddress = hostNames[i] + "::" + ports[i];
                    clientProcessors[i] = ClientProcessor.builder()
                            .hostname(hostNames[i])
                            .port(Integer.parseInt(ports[i]))
                            .dstServerAddress(dstServerAddress)
                            .command(grepCommand)
                            .build();
                    threads[i] = new Thread(clientProcessors[i]);
                    threads[i].start();
                }

                // Wait for all threads to complete
                for (int i = 0; i < numberOfVMs; i++) {
                    threads[i].join();
                }
                System.out.println("Total matched files: " + ClientProcessor.getGrepFileCount().get());
                System.out.println("Total matched lines: " + ClientProcessor.getGrepTotalLineCount().get());
                for (String result : ClientProcessor.getAllGrepResults()) {
                    System.out.println(result);
                }
                stopWatch.stop();
                System.out.println("Execution time: " + stopWatch.prettyPrint());
            }
        } catch (IOException e) {
            System.out.println("Error reading network configuration file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
