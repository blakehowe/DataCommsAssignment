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
       
       System.out.println("\n== To view known chat hosts, type '<HOSTS>' ==\n");
       System.out.println("== To send a file, type '<FILE, ip, fileName>' ==\n");
       System.out.println("== To leave the chat room and exit the program, type '<BYE>' ==\n");
       
       
       System.out.println("\n--- Created By Blake and Daniel --- \n");
       
       //start all the relative client and server threads for the chat.
       startThreads();
       //fileServerCode();
    }
    
    private static void startThreads() {
       try {
           //setup the udp socket that will be used by both the server and clients for the chat.
           DatagramSocket udpSocket = new DatagramSocket(port);
           
           System.out.println("\nSearching for chat users...\n");
           
           //create the user pool object, will maintain the users aswell as be used for authentication
           ChatUserPool userPool = new ChatUserPool(100000, authKey, udpSocket);
           
           //manages sending messages from the user
           Client client = new Client(udpSocket, userPool, tcpport);
           
           
           //manages recieving and co-ordinating recieved messages.
           Server server = new Server(udpSocket, client, authKey, userPool);
           
           //manages receiving files from other chat peers.
           FileServer fileServer = new FileServer(tcpport, userPool);
           
           //let the user know who is already in the chat room, or if they are lonely.
           userPool.printValidHosts();
           
           //start all the threads.
           server.start();
           client.start();
           fileServer.start();
           
           //shutdown hook
           //http://stackoverflow.com/questions/2921945/useful-example-of-a-shutdown-hook-in-java
           //adds a thread to be provoked when a shutdown is initiated, sends a left chat message to all hosts.
           final Thread mainThread = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                client.leaveChat();
                udpSocket.close();
                try {
                     mainThread.join();
                } catch (Exception e) {
                    System.out.println("Error shutting down properly");
                    System.exit(2);
                }
            }
            });
       }
       catch (Exception e) {
           System.out.println("Error creating UDP socket on Port: " + port);
           System.exit(1);
       }
    }
}
