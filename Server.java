import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.*;
/**
 * Write a description of class Server here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 * Message Reciever
 */
public class Server extends Thread
{
    private String auth = "249284222424gerggre23235tijo";
    private DatagramSocket _udpSocket;
    private Client _client;
    
    public Server(DatagramSocket socket, Client client) {
        _udpSocket = socket;
        _client = client;
    }
    
    public void run() {
        //holds the message data from client.
        byte[] recievedBytes = new byte[1024];
        
        DatagramPacket packet;
        String messageString;
        
        while(true) {
            _client.promptUserInput();
            packet = new DatagramPacket(recievedBytes, recievedBytes.length);
            
            try {
                _udpSocket.receive(packet);
                
                //get the IP and Port
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                
                //only print if not recieved from same ip
                if (!address.getHostAddress().equals(CommonFunctions.getIPAddress())) {
                    messageString = new String(packet.getData());
                
                    messageString = messageString.trim();
                    
                    //handle recieved message accordingly
                    if (messageString.startsWith("msg:")) {
                        //remove prefix
                        messageString = messageString.replace("msg:", "");
                        //chat message
                        System.out.print("\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" says: " + messageString);
                    }
                    else if (messageString.equals("authenticate:"+auth)) {
                        //user wants to authenticate with this server 
                        
                        //respond to the user to let them know they are verified?
                    }
                }
                //clear recievedBytes
                recievedBytes = new byte[1024];
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
    }
}
