import java.net.DatagramSocket;
import java.util.concurrent.locks.*;
/**
 * Write a description of class Program here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Program
{
    static int defaultPort = 4000;
    
    public static void main(String[] args) {
       int port = defaultPort;
        try {
           if (args.length > 1) {
               port = Integer.parseInt(args[0]);
           }
           
           //helps keep the readline and println synchronised in console.
           Lock lock = new ReentrantLock();
           
           DatagramSocket udpSocket = new DatagramSocket(port);
           Client client = new Client(udpSocket);
           Server server = new Server(udpSocket, client);
           server.start();
           client.start();
       }
       catch (Exception e) {
           System.out.println("Error creating UDP socket on Port: " + port);
           System.exit(1);
       }
    }
}
