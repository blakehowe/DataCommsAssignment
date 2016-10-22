import java.io.IOException;
import java.net.*;

/**
 * Write a description of class Server here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 * Message Reciever
 */
public class Server extends Thread
{
    private DatagramSocket _udpSocket;
    
    public Server(DatagramSocket socket) {
        _udpSocket = socket;
    }
    
    public void run() {
        //holds the message data from client.
        byte[] recievedBytes = new byte[1024];
        
        DatagramPacket packet;
        String messageString;
        
        while(true) {
            packet = new DatagramPacket(recievedBytes, recievedBytes.length);
            
            try {
                _udpSocket.receive(packet);
                
                //get the IP and Port
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                
                messageString = new String(packet.getData());
                
                messageString = messageString.trim();
                
                System.out.print("\n"+address.toString()+" says: " + messageString);
                
                //clear recievedBytes
                recievedBytes = new byte[1024];
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
}
