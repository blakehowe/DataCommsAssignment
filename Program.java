import java.net.DatagramSocket;
/**
 * Write a description of class Program here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Program
{
    static int port = 4000;
    
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
       
       
       
    }
}
