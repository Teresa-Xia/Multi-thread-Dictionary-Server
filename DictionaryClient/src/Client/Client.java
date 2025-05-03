package Client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import org.json.simple.JSONObject;
import javax.swing.*;

/*Name: Ting Xia
* Student ID: 1427188
* */

public class Client {
    private BufferedWriter out;
    private BufferedReader in;
    static String address;
    static int port;
    private Socket socket;

    public Client(String address, int port) throws IOException {
        this.address = address;
        this.port = port;

        try{
            this.socket = new Socket(address, port);
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            throw new IOException("Unable to connect to server at " + address + ":" + port, e);
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            System.err.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }

        final String address = args[0];
        final int port ;

        try{
            port = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            System.err.println("Port number must be an integer");
            System.exit(1);
            return;
        }

        Client client ;

        try{
            client = new Client(address, port);
        }catch(IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        clientGUI gui = new clientGUI(client);
        while(gui.isRunning()){
            try{
                Thread.sleep(80);
            }catch(InterruptedException e){
                System.err.println("client is interrupted.");
                break;
            }
        }

        client.closeConnection();
    }

    //add
    //request the server to add the given word and corresponding meaning
    public String requestAdd(String word, String meaning) {
        JSONObject obj = new JSONObject();
        obj.put("word", word);
        obj.put("meaning", meaning);

        String response = "";
        try{
            String request = "add " + obj.toJSONString();
            response = sendReceiveRequest(request);

        }catch(Exception e){
            response = "Unable to communicate with the server, please try again.";
            e.printStackTrace();
        }
        return response;
    }

    //query
    public String requestQuery(String word) {
        JSONObject obj = new JSONObject();
        obj.put("word", word);

        String response = "";
        try{
            String request = "query " + obj.toJSONString();
            response = sendReceiveRequest(request);
        }catch(Exception e){
            response = "Unable to communicate with the server, please try again.";
            e.printStackTrace();
        }

        return response;
    }

    //remove
    public String requestRemove(String word) {
        JSONObject obj = new JSONObject();
        obj.put("word", word);

        String response = "";
        try{
            String request = "remove " + obj.toJSONString();
            response = sendReceiveRequest(request);
        }catch(Exception e){
            response = "Unable to communicate with the server, please try again.";
            e.printStackTrace();
        }
        return response;

    }

    //update
    public String requestUpdateMeaning(String word, String oldMeaning, String newMeaning) {
        JSONObject obj = new JSONObject();
        obj.put("word", word);
        obj.put("meaning", oldMeaning);
        obj.put("newMeaning", newMeaning);

        String response = "";
        try{
            String request = "updateMeaning " + obj.toJSONString();
            response = sendReceiveRequest(request);
        }catch(Exception e){
            response = "Unable to communicate with the server, please try again.";
            e.printStackTrace();
        }

        return response;
    }

    //add new meaning
    public String requestAddAdditional(String word, String newMeaning) {
        JSONObject obj = new JSONObject();
        obj.put("word", word);
        obj.put("newMeaning", newMeaning);

        String response = "";
        try{
            String request = "additionMeaning " + obj.toJSONString();
            response = sendReceiveRequest(request);
        }catch(Exception e){
            response = "Unable to communicate with the server, please try again.";
            e.printStackTrace();
        }
        return response;
    }

    public String sendReceiveRequest(String request) throws IOException {
        try{
            if(socket.isClosed()){
                reconnect();
            }

            out.write(request);
            out.newLine();
            out.flush();

            //receive the response from server
            String response = in.readLine();

            return response;

        }catch(SocketException e){
            JOptionPane.showMessageDialog(null, "Connection stop.","Connection Error",JOptionPane.ERROR_MESSAGE);
            try {
                reconnect();
                out.write(request);
                out.newLine();
                out.flush();
                return in.readLine();
            } catch (IOException reconnectException) {
                closeConnection();
                throw new IOException("Failed to send request to the server after reconnecting", reconnectException);
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Error communicating with server: " + e.getMessage(),"Communication Error",JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    public void reconnect() throws IOException{
        if(!socket.isClosed() && socket !=null){
            socket.close();
        }

        this.socket = new Socket(address, port);
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void closeConnection() throws IOException {
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}