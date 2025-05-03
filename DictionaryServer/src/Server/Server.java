package Server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ServerSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*Name: Ting Xia
 * Student ID: 1427188
 * */

public class Server {
    protected static String result;
    protected static JSONObject dict;
    private ServerSocket serverSocket;
    private boolean running = false;
    private ExecutorService threadPool;
    private static serverGUI gui;

    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            System.err.println("Usage: java -jar DictionaryServer.jar <port> <dictionary_file_path>");
            System.exit(1);
        }

        final int port = Integer.parseInt(args[0]);
        final String filepath = args[1];

        Server server = new Server();

        server.gui = new serverGUI(server);
        SwingUtilities.invokeLater(() -> {
            server.gui.setVisible(true);
            String message = "";
            server.gui.updateStatus(message);
        });

        server.startServer(port, filepath);
    }

    public void startServer(int port, String filepath) {
        running = true;
        //create a thread pool
        threadPool = Executors.newFixedThreadPool(13);

        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try{
            serverSocket = factory.createServerSocket(port);
            System.out.println("Listening on port " + port);
            int i = 0;
            while(running){
                Socket clientSocket = serverSocket.accept();
                i++;
                String message = "Client " + i + " are connected!";
                System.out.println(message);
                if(gui != null){
                    gui.updateStatus(message);
                }
                //using server() to handle client
                threadPool.submit(() -> server(clientSocket,filepath));
            }
        }catch(IOException e){
            if(running){
                e.printStackTrace();
            }
        }finally {
            stopServer();
        }

    }

    public void stopServer() {
        running = false;
        if(serverSocket != null && !serverSocket.isClosed()){
            try{
                serverSocket.close();
                System.out.println("Server closed!");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        if(threadPool != null){
            threadPool.shutdown();
        }
    }

    private static void server(Socket clientSocket, String filePath) {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ){
            JSONParser parser = new JSONParser();
            String request = in.readLine();

            try{
                String action = request.substring(0,request.indexOf(' '));
                String data = request.substring(request.indexOf(' ')+1);

                JSONObject jsonObject = (JSONObject)parser.parse(data);

                String word = (String)jsonObject.get("word");
                String meaning = (String)jsonObject.get("meaning");
                String newMeaning = (String)jsonObject.get("newMeaning");

                switch(action){
                    case "add":
                        result = add(word, meaning, filePath);
                        break;
                    case "query":
                        result = query(word,filePath);
                        break;
                    case "additionMeaning":
                        result = additionMeaning(word,newMeaning,filePath);
                        break;
                    case "updateMeaning":
                        result = updateMeaning(word, meaning, newMeaning,filePath);
                        break;
                    case "remove":
                        result = remove(word,filePath);
                        break;
                    default:
                        result = "Unknown action: " + action;
                }

                JSONObject reply = new JSONObject();
                reply.put("result", result);
                out.write(reply.toJSONString());
                out.newLine();
                out.flush();

            }catch(ParseException | IOException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(gui != null){
                String message = "Client disconnected.";
                System.out.println(message);
                gui.updateStatus(message);
            }
        }
    }

    /*remove the word and meaning from dictionary file*/
    public static synchronized String remove(String word, String filepath) {
        dict = readFile(filepath);
        if(dict == null){
            return "Error reading dictionary file : " + filepath;
        }else {
            if (dict.containsKey(word.toLowerCase())) {
                dict.remove(word.toLowerCase());
                if (writeDict(dict, filepath)) {
                    return "Removed " + word + " from " + filepath + " successfully";
                } else {
                    return "Error writing to dictionary file : " + filepath;
                }
            } else {
                return word + " does not exist! ";
            }
        }
    }

    /*update the meaning of an existing word in the dictionary file*/
    public static synchronized String updateMeaning(String word, String oldMeaning, String newMeaning, String filepath) {
        dict = readFile(filepath);
        if(dict == null){
            return "Error reading dictionary file : " + filepath;
        }

        JSONArray meanings = (JSONArray) dict.get(word.toLowerCase());
        if(meanings == null){
            return word + " does not exist! ";
        }

        int index = meanings.indexOf(oldMeaning);
        if(index == -1){
            return oldMeaning + " does not exist! ";
        }else{
            meanings.set(index, newMeaning);
            dict.put(word.toLowerCase(), meanings);

            if(writeDict(dict, filepath)){
                return "Meaning updated successfully";
            }else{
                return "Error writing to dictionary file.";
            }
        }
    }

    /*add additional meaning to dictionary file*/
    public static synchronized String additionMeaning(String word, String newMeaning, String filepath) {
        dict = readFile(filepath);
        if(dict == null){
            return "Error reading dictionary file : " + filepath;
        }

        JSONArray meanings = (JSONArray)dict.get(word.toLowerCase());
        if(meanings == null){
            return word + " does not exist!";
        }

        if(newMeaning == null | newMeaning.isEmpty()){
            return "New meaning is empty!";
        }

        //check new additional meaning is existed or not
        if(meanings.contains(newMeaning)){
            return newMeaning + " already exist!";
        }else {
            meanings.add(newMeaning);
            dict.put(word.toLowerCase(), meanings);

            if (writeDict(dict, filepath)) {
                return "The word " + word + " additional meaning is successfully added.";
            } else {
                return "Error writing to dictionary file : " + filepath;
            }
        }
    }
    /*query a word*/
    public static synchronized String query(String word, String filepath) {
        dict = readFile(filepath);
        if(dict == null){
            return "Error reading dictionary file : " + filepath;
        }

        JSONArray meanings = (JSONArray) dict.get(word.toLowerCase());
        if(meanings == null){
            return word + " does not exist!";
        }else{
            return word + " found! Meanings : " + meanings.toString();
        }
    }

    public static synchronized String add(String word, String meaning, String filepath) {
        JSONObject dict = readFile(filepath);
        if(dict == null){
            return "Error reading dictionary file : " + filepath;
        }

        JSONArray meanings = (JSONArray) dict.get(word.toLowerCase());
        if(meanings == null){
            meanings = new JSONArray();
            dict.put(word.toLowerCase(), meanings);
        }

        if(meanings.contains(meaning)){
            return meaning + " already exists for " + word + " !";
        }else{
            meanings.add(meaning);
        }

        if(writeDict(dict, filepath)){
            return "The word " + word + " and it's meaning are successfully added.";
        }else{
            return "Error writing to dictionary file : " + filepath;
        }
    }

    /*read the dictionary file*/
    public static JSONObject readFile(String filepath){
        JSONParser parser = new JSONParser();
        JSONObject dict = null;

        try(FileReader reader = new FileReader(filepath)){
            dict = (JSONObject) parser.parse(reader);
        }catch(IOException | ParseException e){
            e.printStackTrace();
        }
        return dict;
    }

    /*write to dictionary file*/
    public static boolean writeDict(JSONObject dict, String filepath){
        try(FileWriter w = new FileWriter(filepath)){
            w.write(dict.toJSONString());
            w.flush();
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
