import java.io.*;
import java.net.*;

public class ShutdownClient {
    public static void main(String[] args) {
        String serverIP = "192.168.115.52"; // Replace with actual server IP
        int port = 5000;
        String uid = "Lab-PC-01"; // Unique ID for the client machine

        try (Socket socket = new Socket(serverIP, port);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeUTF(uid); // Send UID to server

            while (true) {
                String command = dis.readUTF();
                if (command.equalsIgnoreCase("shutdown")) {
                    System.out.println("Shutdown command received. Shutting down...");
                    Runtime.getRuntime().exec("shutdown -s -t 0"); // For Windows
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}