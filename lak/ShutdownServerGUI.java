import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ShutdownServerGUI extends JFrame {

    private DefaultListModel<String> clientListModel;
    private Map<String, DataOutputStream> clientMap = new ConcurrentHashMap<>();

    public ShutdownServerGUI() {
        setTitle("Lab Shutdown Controller");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        clientListModel = new DefaultListModel<>();
        JList<String> clientList = new JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientList);

        JButton shutdownAllButton = new JButton("Shutdown All");
        shutdownAllButton.addActionListener(e -> sendShutdownCommandToAll());

        add(scrollPane, BorderLayout.CENTER);
        add(shutdownAllButton, BorderLayout.SOUTH);

        new Thread(this::startServer).start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            String uid = dis.readUTF();
            clientMap.put(uid, dos);

            SwingUtilities.invokeLater(() -> clientListModel.addElement(uid));

            System.out.println("Client connected: " + uid);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendShutdownCommandToAll() {
        for (Map.Entry<String, DataOutputStream> entry : clientMap.entrySet()) {
            try {
                entry.getValue().writeUTF("shutdown");
                System.out.println("Shutdown sent to " + entry.getKey());
            } catch (IOException e) {
                System.out.println("Failed to send shutdown to " + entry.getKey());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShutdownServerGUI().setVisible(true));
    }
}