package org.example.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;

    public Client(Socket socket, String username) throws IOException {
        this.socket = socket;
        this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = username;
    }

    public void sendMessage(){

        printWriter.write(username);
        printWriter.println();
        printWriter.flush();

        Scanner scanner = new Scanner(System.in);

        while(socket.isConnected()){

            String messageToBeSent = scanner.nextLine();
            printWriter.write(username + " : " + messageToBeSent);
            printWriter.println();
            printWriter.flush();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while (socket.isConnected()){

                    try{

                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, printWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter bufferedWriter){

        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Username: ");

        String username = scanner.nextLine();
        Socket socket = new Socket("Localhost", 2222);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}