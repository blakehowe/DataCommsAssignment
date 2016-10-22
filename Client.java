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
        checkHosts();
        System.out.println(_hosts);
    }
    
    private void sendMessage(String address, String message) {
        //append so server knows how to handle.
        message = "msg:" + message;
        //convert the message string into bytes to be transferred with the datagram.
        byte[] messageBytes = message.getBytes();
        if (message.trim() != "") {
            try {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(address), 4000);
            
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
            System.out.print("\n"+CommonFunctions.getIPAddress()+": ");
    }
    
    public void checkHosts() {
       List<String> newHosts = new ArrayList<String>();
       
       String subnet = CommonFunctions.getSubnetMask();
       //System.out.println(subnet);
       int hostNum = CommonFunctions.getHostNumber();
       //System.out.println(hostNum);
       
       //calc 10 before
       int hostNumBefore = hostNum - 10;
       if (hostNumBefore < 0) {
           hostNumBefore = 0;
       }
       
       //calc 10 after
       int hostNumAfter = hostNum + 10;
       if (hostNumAfter > 254) {
           hostNumAfter = 254;
       }
       
       
       
       
       //try reaching the hosts within the range using isReachable
       int timeout=100;
       for (int i = hostNumBefore; i < hostNumAfter + 1; i++){
           String host=subnet + "." + i;
           //System.out.println(host);
           try {
            if (InetAddress.getByName(host).isReachable(timeout)){
               //System.out.println(host + " is reachable");
               
               //TODO: VALIDATE HOST IS USING THE SAME APPLICATION? UNIQUE STRING?
               newHosts.add(host);
            }
           } catch (Exception e) {
               System.out.println(host + " is unreachable");
           }
           
       }
       
       
       
       _hosts = newHosts;
    }
}
