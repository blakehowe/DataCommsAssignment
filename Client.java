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
    private int _fileTransferPort;
    
    public Client(DatagramSocket socket, ChatUserPool userPool, int tcpPort) {
        _socket = socket;
        _userPool = userPool;
        _fileTransferPort = tcpPort;
    }
    
    
    
    private void sendMessage(String address, String message) {
        //needs to know how to handle the message
        
        //is it a direct message?
        if (!message.startsWith("dmsg:")) {
            //is it a bye message?
            if (!message.equals("<BYE>")) {
                //is it a left chat message?
                if (!message.equals("<LEFTCHAT>")) {
                    //if none of those, send it as a regular message.
                    message = "msg:" + message;
                }
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
            System.out.println("You have left the chat room");
            
            //shutdown hook should do its job of letting users know its leaving
            System.exit(0);
        }
        
    }
    
    private void directMessage(String message) {
        //example: @192.1.1.1 message
        //handle a message going to an individual host.
                    
        String toSplit = message.replace("@", "");
        String[] split = toSplit.split(" ");
        String ip = split[0];
        String extractMessage = split[1];
                    
        //System.out.println("IP: "+ip);
        //System.out.println("Message: "+extractMessage);
        //check if valid user
        if (_userPool.isAuthenticatedUser(ip)) {
             //System.out.println("VALID");
             
             //prepare message
             String directMessage = "dmsg:"+extractMessage;
             
             //send the message to only that user.
             sendMessage(ip, directMessage);
             
             System.out.println("Direct messaged: "+ip+"!");
        } 
        else {
             System.out.println("Sorry, cannot direct message to "+ip+", it is not a known chat host");
        }
    }
    
    private void sendFile(String message) {
        //remove arrows in the message
        String toSplit = message.replace("<", "");
        toSplit = toSplit.replace(">", "");
        
        //split up the string so can extract needed info
        String[] splitted = toSplit.split(",",3);
        //make sure correct parameters.
        if (splitted.length == 3) {
            String ip = splitted[1];
            String filename = splitted[2];
            
            //check if ip is authenticated?
            if (_userPool.isAuthenticatedUser(ip)) {
                //pass ip and filename to file sender class.
                FileSender filesender = new FileSender(ip, _fileTransferPort, filename);
                
                //start sending the file asynchronously.
                filesender.start();
            }
            else {
                System.out.println("Cannot file transfer to unrecognised host: " + ip);
            }
        }
        else {
            //invalid format
        }
        
    }
    
    
    public void leaveChat() {
        //let all hosts know you have left the chat.
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
                if (message.startsWith("@")) {
                    directMessage(message);
                }
                if (message.startsWith("<FILE") && message.endsWith(">")) {
                    sendFile(message);
                }
                else if (message.equals("<HOSTS>")) {
                    _userPool.printValidHosts();
                }
                else if (message.equals("<REFRESH>")) {
                    System.out.println("\nForced refresh of chat users...\n");
                    _userPool.updateList();
                }
                else {
                    broadcastMessage(message);
                }
            }
            catch (IOException e) {
                System.err.println("Commandline input error, try again?");
            }
        }
    }
    
    public static void promptUserInput() {
        // prompts user that they can send a message
        System.out.print("\n"+CommonFunctions.getHostName()+" ("+CommonFunctions.getIPAddress()+")"+": ");
    }
}
