import lombok.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private List<String> options;
    @Getter
    private volatile static AtomicInteger grepTotalLineCount = new AtomicInteger(0);
    @Getter
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

            // Get grep result as byte array
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // Read byte array length
            int length = dataInputStream.readInt();
            byte[] grepCommandResultBytes = new byte[length];
            String grepResult = "";
            if (length > 0) {
                dataInputStream.readFully(grepCommandResultBytes);
                grepResult = new String(grepCommandResultBytes, StandardCharsets.UTF_8);
            }
            int exitCode = dataInputStream.readInt();

            if (options.contains("q")) {
                if (exitCode == 0) {
                    incrGrepFileCount(1);
                }
            } else if (exitCode == 0) {
                if (options.contains("l")) {
                    if (!grepResult.isEmpty()) {
                        incrGrepFileCount(1);
                        allGrepResults.add("Server: " + dstServerAddress + "\n" + grepResult);
                    }
                } else if (options.contains("L")) {
                    if (!grepResult.contains("Matched lines: ") || grepResult.contains("Matched lines: 0")) {
                        incrGrepFileCount(1);
                        allGrepResults.add("Server: " + dstServerAddress + "\n" + grepResult);
                    }
                } else if (grepResult.contains("Matched lines: ") && !grepResult.contains("Matched lines: 0")) {
                    incrGrepFileCount(1);
                    int matchedLineCount = getGrepLineCount(grepResult);
                    incrGrepTotalLineCount(matchedLineCount);
                    allGrepResults.add("Server: " + dstServerAddress + "\n" + grepResult);
                }
            }
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

    private static synchronized void incrGrepFileCount(int count) {
        grepFileCount.addAndGet(count);
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
}
