package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/*Name: Ting Xia
 * Student ID: 1427188
 * */

public class clientGUI extends JFrame{
    private JPanel clientPanel;
    private JTextField wordTxt;
    private JButton addBtn;
    private JButton queryBtn;
    private JButton removeBtn;
    private JButton updateBtn;
    private JButton addNewMeaningBtn;
    private JTextArea meaningArea;
    private JTextArea newMeaningArea;
    protected static Client client;
    private boolean isRunning;

    public clientGUI(Client client){
        this.client = client;
        this.isRunning = true;

        /*when window is closed by user, isRunning return false*/
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isRunning = false;
                try{
                    //close client connection
                    client.closeConnection();
                }catch( IOException ioException){
                    ioException.printStackTrace();
                }
            }
        });

        setContentPane(clientPanel);
        setTitle("Dictionary Application -- Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        addBtn.setBounds(282, 195, 93, 23);
        queryBtn.setBounds(282, 195, 93, 23);
        removeBtn.setBounds(282, 195, 93, 23);
        updateBtn.setBounds(282, 195, 93, 23);
        addNewMeaningBtn.setBounds(282, 195, 93, 23);

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTxt.getText();
                String meaning = meaningArea.getText();
//check word and meaning is empty or not
                if(word.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter the word.");
                }else{
                    if(meaning.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Please enter the meaning of the word.");
                    }else{
                        String response = client.requestAdd(word, meaning);
                        JOptionPane.showMessageDialog(null, response);
                        wordTxt.setText("");
                        meaningArea.setText("");
                    }
                }
            }
        });
        queryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTxt.getText();

                if(word.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter the word.");
                }else{
                    String response = client.requestQuery(word);
                    JOptionPane.showMessageDialog(null, response);
                    wordTxt.setText("");
                }
            }
        });
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTxt.getText();

                if(word.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter the word.");
                }else{
                    String response = client.requestRemove(word);
                    JOptionPane.showMessageDialog(null, response);
                    wordTxt.setText("");
                }
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTxt.getText();
                String oldMeaning = meaningArea.getText();
                String newMeaning = newMeaningArea.getText();
        //check word and meaning are not empty
                if(word.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter the word.");
                }else{
                    if(oldMeaning.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Please enter the old meaning of the word.");
                    }else{
                        if(newMeaning.isEmpty()){
                            JOptionPane.showMessageDialog(null, "Please enter the new meaning of the word.");
                        }else{
                            String response = client.requestUpdateMeaning(word, oldMeaning, newMeaning);
                            JOptionPane.showMessageDialog(null, response);
                            wordTxt.setText("");
                            meaningArea.setText("");
                            newMeaningArea.setText("");
                        }
                    }
                }
            }
        });
        addNewMeaningBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordTxt.getText();
                String meaning = meaningArea.getText();
                String newMeaning = newMeaningArea.getText();

                if(word.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter the word.");
                }else{
                    if(newMeaning.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Please enter the new meaning of the word.");
                    }else{
                        if(!meaning.isEmpty()){
                            JOptionPane.showMessageDialog(null, "Please keep the old meaning of the word area empty.");
                        }else{
                            String response = client.requestAddAdditional(word, newMeaning);
                            JOptionPane.showMessageDialog(null, response);
                            wordTxt.setText("");
                            newMeaningArea.setText("");
                        }

                    }
                }
            }
        });
        setVisible(true);
    }

    public static void window(Client client) {
        EventQueue.invokeLater(() -> {
            try{
                clientGUI window = new clientGUI(client);
                window.setVisible(true);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public boolean isRunning(){
        return isRunning;
    }
}
