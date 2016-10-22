import java.net.DatagramSocket;
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
    static int port = 4000;
    static int tcpport = 4001;

    static String inetAddress = "127.0.0.1"; 
    
    public static void main(String[] args) {
       try {
           DatagramSocket udpSocket = new DatagramSocket(port);
       
           Server server = new Server(udpSocket);
           
           server.start();
           Client client = new Client(udpSocket);
           
           client.start();
       }
       catch (Exception e) {
           System.out.println("Error creating UDP socket on Port: " + port);
           System.exit(1);
       }
       
       //file sender and receiver initialisation
       try {
       		ServerSocket tcpServerSocket = new ServerSocket(tcpport);
            FileServer fileserver = new FileServer(tcpServerSocket);
            fileserver.start();

            System.out.println('\n' + "Server started.");

       		Socket tcpSocket = new Socket(inetAddress, tcpport);
            FileSender filesender = new FileSender(tcpSocket);
            filesender.start();

            System.out.println("Client started.");
       } 
       catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
       }
    }
}
