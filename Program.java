import java.net.DatagramSocket;
import java.util.concurrent.locks.*;

import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Write a description of class Program here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Program
{
    //a unique code that helps ensure the peers know each other and can authenticate each other.
    static String authKey = "249284222424gerggre23235tijo";
    
    static int port = 4000;
    static int tcpport = 4001;
    
    static String inetAddress = "127.0.0.1";
    
    public static void main(String[] args) {
        //allow the user to define a different chatport, tcp file transfer port & authkey, if no arguments passed, the defaults will be used.
       if (args.length >= 1) {
           //user has specified chat port to use
           port = Integer.parseInt(args[0]);
       }
       
       if (args.length >= 2) {
           //user has specified filetransfer port to use
           tcpport = Integer.parseInt(args[1]);
       }
       
       if (args.length == 3) {
           //user has specified a custom auth key
           authKey = args[2];
       }
       
       System.out.println("--- Welcome to the Chat & File Transfer Application ---");
       if (args.length == 0) {
           System.out.println("\n-- Default Settings are being used --\n= You can run this chat program with arguments, <ChatPort> <FTPort> <AuthKey> =");
       }
       System.out.println("\n== Using UDP Port "+port+" for Chatting ==");
       System.out.println("== Using TCP Port "+tcpport+" for File Transfers ==");
       System.out.println("== Using Key "+authKey+" for Authentication Security ==");
       
       
       System.out.println("\n--- Created By Blake and Daniel --- \n");
       
       //start all the relative client and server threads for the chat.
       startChatThreads();
    }
    
    private static void startChatThreads() {
       try {
           //setup the udp socket that will be used by both the server and clients for the chat.
           DatagramSocket udpSocket = new DatagramSocket(port);
           
           //create chat user pool
           ChatUserPool userPool = new ChatUserPool(100000, authKey, udpSocket);
           
           Client client = new Client(udpSocket);
           Server server = new Server(udpSocket, client, authKey);
           server.start();
           client.start();
       }
       catch (Exception e) {
           System.out.println("Error creating UDP socket on Port: " + port);
           System.exit(1);
       }
    }
    
    private static void fileServerCode() {
        //file sender and receiver initialisation
       try {
       		ServerSocket tcpServerSocket = new ServerSocket(tcpport);
            FileServer fileserver = new FileServer(tcpServerSocket);
            fileserver.start();

            //System.out.println('\n' + "Server started.");

       		Socket tcpSocket = new Socket(inetAddress, tcpport);
            FileSender filesender = new FileSender(tcpSocket);
            filesender.start();

            //System.out.println("Client started.");
       } 
       catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
       }
    }
}
