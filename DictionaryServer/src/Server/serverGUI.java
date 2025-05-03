package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*Name: Ting Xia
 * Student ID: 1427188
 * */

public class serverGUI extends JFrame {
    private JButton stopServerBtn;
    private JTextArea logArea;
    private JTextArea statusArea;
    private JLabel statusLabel;
    private Server server;
    private boolean running;

    public serverGUI(Server server) {
        this.server = server;
        this.setTitle("Dictionary application -- server");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //create stop server button
        stopServerBtn = new JButton("Stop Server");
        stopServerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea),BorderLayout.CENTER);

        //create status labels
        statusLabel = new JLabel("Server Status");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JPanel panel = new JPanel();
        panel.add(stopServerBtn);
        add(panel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
        setVisible(true);

        this.running = true;
    }

    public void updateStatus(String message) {
        statusArea.append(message + "\n");
    }

    public boolean isRunning() {
        return running;
    }

    private void stopServer() {
        this.running = false;
        server.stopServer();
        statusLabel.setText("Status: server stopped");
        stopServerBtn.setEnabled(false);
    }
}
