import lombok.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @className: ClientProcessor
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProcessor implements Runnable{
    private String hostname;
    private int port;
    private String command;
    private String dstServerAddress;
    private volatile static AtomicInteger grepTotalLineCount = new AtomicInteger(0);
    private volatile static AtomicInteger grepFileCount = new AtomicInteger(0);
    @Getter
    private static List<String> allGrepResults = Collections.synchronizedList(new ArrayList<>());
    public void run() {
        try {
            // Send grep command
            Socket socket = new Socket(hostname, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(command);
            dataOutputStream.flush();

            // Get grep result
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String grepResult = dataInputStream.readUTF();
            if (!grepResult.isEmpty()) {
                grepFileCount.incrementAndGet();
            }
            int matchedLineCount = getGrepLineCount(grepResult);
            incrGrepTotalLineCount(matchedLineCount);
            allGrepResults.add("Server: " + dstServerAddress + "\n" + grepResult);
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Server " + dstServerAddress + " is down or unreachable. Skipping this server.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static synchronized void incrGrepTotalLineCount(int count) {
        grepTotalLineCount.addAndGet(count);
    }

    private int getGrepLineCount (String grepResult) {
        String[] lines = grepResult.split("\n");
        for (String line : lines) {
            if (line.startsWith("Matched lines: ")) {
                return Integer.parseInt(line.replace("Matched lines: ", ""));
            }
        }
        return 0;
    }

    public static int getGrepFileCount() {
        return grepFileCount.get();
    }

    public static int getGrepTotalLineCount() {
        return grepTotalLineCount.get();
    }
}
