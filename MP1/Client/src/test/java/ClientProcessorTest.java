import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @className: ClientProcessorTest
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */
public class ClientProcessorTest {
    @BeforeEach
    public void setUp() {
        ClientProcessor.getGrepFileCount().set(0);
        ClientProcessor.getGrepTotalLineCount().set(0);
        ClientProcessor.getAllGrepResults().clear();
    }
    @Test
    public void test_ClientProcessor() throws Exception{
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            // Simulate server thread
            Thread thread = new Thread(() -> {
                try {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    String command = dataInputStream.readUTF();
                    System.out.println("Receive the command: " + command);
                    // Simulate get a grep result
                    String grepResult = "Matched lines: 3\nError occurred\nError happened\nError stopped";
                    DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                    dataOutputStream.writeUTF(grepResult);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    clientSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            // Simulate client thread
            ClientProcessor clientProcessor = ClientProcessor.builder()
                    .hostname("localhost")
                    .port(4444)
                    .command("grep ERROR log.txt")
                    .dstServerAddress("localhost::4444")
                    .build();
            Thread clientProcessorThread = new Thread(clientProcessor);
            clientProcessorThread.start();
            clientProcessorThread.join();

            Assertions.assertEquals(1, ClientProcessor.getGrepFileCount().get(), "File count should be 1");
            Assertions.assertEquals(3, ClientProcessor.getGrepTotalLineCount().get(), "Total matched lines should be 3");
            Assertions.assertFalse(ClientProcessor.getAllGrepResults().isEmpty(), "There should be some grep results.");
            System.out.println("Grep File Count: " + ClientProcessor.getGrepFileCount().get());
            System.out.println("Grep Total Line Count: " + ClientProcessor.getGrepTotalLineCount().get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_ClientProcessorServerDown() {
        ClientProcessor clientProcessor = ClientProcessor.builder()
                .hostname("localhost")
                .port(1986)
                .command("grep ERROR log.txt")
                .dstServerAddress("localhost::1986")
                .build();
        Thread clientProcessorThread = new Thread(clientProcessor);
        clientProcessorThread.start();
        try {
            clientProcessorThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        Assertions.assertEquals(0, ClientProcessor.getGrepFileCount().get(), "File count should be 0 when server is down");
        Assertions.assertEquals(0, ClientProcessor.getGrepTotalLineCount().get(), "Total matched lines should be 0 when server is down");
        System.out.println("GrepFileCount: " + ClientProcessor.getGrepFileCount());
        System.out.println("GrepTotalLineCount: " + ClientProcessor.getGrepTotalLineCount());
    }
}

