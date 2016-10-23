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
    private ChatUserPool _userPool;
    
    public Client(DatagramSocket socket, ChatUserPool userPool) {
        _socket = socket;
        _userPool = userPool;
    }
    
    
    
    private void sendMessage(String address, String message) {
        //append so server knows how to handle.
        if (!message.equals("<BYE>")) {
            if (!message.equals("<LEFTCHAT>")) {
                message = "msg:" + message;
            }
        }
        
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
        for (String host : _userPool.validHosts()) {
            sendMessage(host, message);
        }
        
        if (message.equals("<BYE>")) {
            //close the application
            System.err.println("You have left the chat room");
            
            //shutdown hook should do its job of letting users know
            
            System.exit(0);
        }
        
    }
    
    public void leaveChat() {
        broadcastMessage("<LEFTCHAT>");
    }
    
    public void run() {
        while (true) {
            // http://stackoverflow.com/questions/12999899/getting-user-input-with-scanner
            //promptUserInput();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String message = "";
            try {
                promptUserInput();
                message = reader.readLine();
                if (!message.equals("<HOSTS>")) {
                    broadcastMessage(message);
                }
                else {
                    _userPool.printValidHosts();
                }
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
