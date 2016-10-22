import java.net.*;
import java.io.*;
import java.util.*;
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
        //convert the message string into bytes to be transferred with the datagram.
        byte[] messageBytes = message.getBytes();
        
        try {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(address), 4000);
            
            _socket.send(packet);
        } catch (Exception e) {
            System.err.println("Error: Unable to send message to - " + address);
        }
    }
    
    private void broadcastMessage(String message) {
        //send the message to each known host.
        for (String host : _hosts) {
            sendMessage(host, message);
        }
    }
    
    public void run() {
        while(true) {
            //get a message to send from the user.
            String messageToSend = promptUserInput();
            
            broadcastMessage(messageToSend);
        }    
    }
    
    public static String promptUserInput() {
            // prompts user that they can send a message
            System.out.print("\n"+CommonFunctions.getIPAddress()+": Enter message" + " => ");
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            
            return message;
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
       
       //try reaching the hosts within the range using pings
       for (int i = hostNumBefore; i < hostNumAfter + 1; i++){
           String host=subnet + "." + i;
           try {
               if (isReachable(host)) {
                   System.out.println(host + " is reachable");
                   newHosts.add(host);
               }
           }
           catch(Exception e) {
               System.out.println(host + " is unreachable");
           }
       }
       
       /*
       //try reaching the hosts within the range using isReachable
       int timeout=3000;
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
       
       */
       
       _hosts = newHosts;
    }
    
    //http://stackoverflow.com/questions/18321118/best-alternative-for-inetaddress-getbynamehost-isreachabletimeout
    public boolean isReachable(String hostname) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(
            "cmd /C ping -n 1 "+hostname+" | find \"TTL\""
    );
    return (p.waitFor() == 0);
}
}
