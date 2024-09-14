import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @className: Server
 * @author: Peregrine Calder
 * @description: TODO
 * @version: 1.0
 */
public class Server {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = Integer.parseInt(args[0]);
        String address = hostname + "::" + port;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            boolean status = true;
            while (status) {
                // Wait for Client connection
                Socket socket = serverSocket.accept();
                System.out.println("Connected to client: " + socket.getRemoteSocketAddress());
                // Receive command from Client
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String command = dataInputStream.readUTF();
                System.out.println("Received grep command: " + command);
                // Execute grep command and get results
                GrepHandler grepHandler = new GrepHandler();
                byte[] grepCommandRes = grepHandler.grep(command);
                int exitCode = grepHandler.getExitCode();
                // Send result back to client
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                dataOutputStream.writeInt(grepCommandRes.length);
                dataOutputStream.write(grepCommandRes);
                dataOutputStream.writeInt(exitCode);
                dataOutputStream.flush();
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
