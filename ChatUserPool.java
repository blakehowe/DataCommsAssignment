import java.util.*;
import java.io.*;
import java.net.*;
/**
 * Write a description of class ChatUserPool here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ChatUserPool extends Thread
{
   private List<String> _validHosts;
   
   //the refresh interval of checking
   private int _refreshMs;
   private String _authKey;
   private DatagramSocket _socket;
    
   public ChatUserPool(int refreshMs, String authKey, DatagramSocket socket) {
       _refreshMs = refreshMs;
       _authKey = authKey;
       _socket = socket;
       
       updateList();
       printValidHosts();
   }
   
   public void printValidHosts() {
       System.out.println("Valid Hosts: " + _validHosts);
   }
   
   public void run() {
       //if thread is started, this will refresh the hosts list by the refresh interval
       while (true) {
           try {
               Thread.sleep(_refreshMs);
           } catch (Exception e) {}
           
           updateList();
       }
   }
   
   private Boolean authenticateUser(String hostAddress) {
       //http://stackoverflow.com/questions/12363078/adding-timeout-to-datagramsocket-receive
       if (hostAddress.equals(CommonFunctions.getIPAddress())) {
           return false;
       }
       int timeout = 2000;
       
       Boolean result = false;
       
       //send auth request with the auth key
       //append so server knows its a auth request
        String message = "authenticate:" + _authKey;
        
        //convert the message string into bytes to be transferred with the datagram.
        byte[] messageBytes = message.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(hostAddress), _socket.getLocalPort());
            
            _socket.send(packet);
        } catch (Exception e) {
            System.err.println("Error: Unable to send authentication request to - " + hostAddress + ":" + _socket.getLocalPort());
        }
        
       //wait for a true, false response
       byte[] recievedBytes = new byte[1024];
       DatagramPacket receivePacket = new DatagramPacket(recievedBytes, recievedBytes.length);
       
       
        try {
            //wait for a certain amount of time for a response before timing out
            _socket.setSoTimeout(timeout);
            
            _socket.receive(receivePacket);
            
            String recievedString = new String(receivePacket.getData());
            recievedString = recievedString.trim();
       
            //make sure a authenticateResponse was recieved
            if (recievedString.startsWith("authenticateResponse:")) {
                String resultString = recievedString.replace("authenticateResponse:", "");
                if (resultString.equals("1")) {
                    //user was authenticated by the host
                    result = true;
                }
            }
        } catch (SocketTimeoutException e) {
            //the host never responded with an auth response :-(
        } catch (Exception e) {
            //unknown socket error
            System.out.println(e.getMessage());
        }
        
       return result;
   }
   
   private void updateList() {
       List<String> activeHosts = getActiveHosts();
       
       List<String> validHosts = new ArrayList<String>();
       
       //for each active host
       //check if authentication passes
       for (String host : activeHosts) {
           //if host is not self
           if (!host.equals(CommonFunctions.getIPAddress())) {
               if (authenticateUser(host)) {
                //authenticated, add to validHosts list
                validHosts.add(host);
                System.out.println("Authenticated by "+host);
                } else {
                    //didn't authenticate
                    System.out.println("Unable to authenticate with "+host);
                }
           }
       }
       
       //set new validHosts
       _validHosts = validHosts;
   }
   
   private List<String> getActiveHosts() {
       List<String> newHosts = new ArrayList<String>();
       
       String subnet = CommonFunctions.getSubnetMask();
       int hostNum = CommonFunctions.getHostNumber();
       
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
       
       return newHosts;
    }
}
