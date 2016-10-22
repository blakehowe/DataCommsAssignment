import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 * Message Sender
 */
public class Client extends Thread
{
    //send messages
    private DatagramSocket _socket;
    public List<String> _hosts;
    private int _port;
    
    public Client(DatagramSocket socket) {
        _socket = socket;
        _hosts = new ArrayList<String>();
    }
    
    private void sendMessage(String address, String message) {
        //append so server knows how to handle.
        message = "msg:" + message;
        //convert the message string into bytes to be transferred with the datagram.
        byte[] messageBytes = message.getBytes();
        if (message.trim() != "") {
            try {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(address), _socket.getLocalPort());
            
            _socket.send(packet);
            } catch (Exception e) {
                System.err.println("Error: Unable to send message to - " + address);
            }
        }
    }
    
    private void broadcastMessage(String message) {
        //send the message to each known host.
        for (String host : _hosts) {
            sendMessage(host, message);
        }
    }
    
    public void run() {
        while (true) {
            //promptUserInput();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String message = "";
            try {
                message = reader.readLine();
                broadcastMessage(message);
            }
            catch (IOException e) {
                System.err.println("Commandline input error, try again?");
            }
        }
    }
    
    public void promptUserInput() {
        // prompts user that they can send a message
            System.out.print("\n"+CommonFunctions.getHostName()+" ("+CommonFunctions.getIPAddress()+")"+": ");
    }
}
