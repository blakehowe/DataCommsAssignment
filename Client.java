import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Client extends Thread
{
    private DatagramSocket _socket;
    public List<String> _hosts;
    
    public Client(DatagramSocket socket) {
        _socket = socket;
        _hosts = new ArrayList<String>();
        checkHosts();
        System.out.println(_hosts);
    }
    
    public void run() {
        while(true) {
            //get a message to send from the user.
            String messageToSend = promptUserInput();
        }    
    }
    
    public String promptUserInput() {
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
       //System.out.println(hostNumBefore);
       
       //calc 10 after
       int hostNumAfter = hostNum + 10;
       if (hostNumAfter > 254) {
           hostNumAfter = 254;
       }
       //System.out.println(hostNumAfter);
       
       //calculate 10 before, 10 after.
       int timeout=50;
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
