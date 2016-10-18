import java.net.*;
import java.io.*;
import java.util.*;
import java.net.InetAddress;
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Client
{
    private DatagramSocket _socket;
    public List<String> _hosts;
    private int _port = 4000;
    
    public Client() {
        try {
            _socket = new DatagramSocket(_port);
        }
        catch (Exception e) {
            System.out.println("Error creating UDP socket on Port: " + _port);
            System.exit(1);
        }
        finally {
            _hosts = new ArrayList<String>();
            checkHosts();
            System.out.println(_hosts);
        }
        
    }
    
    public void checkHosts(){
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
               _hosts.add(host);
            }
           } catch (Exception e) {
               System.out.println(host + " is unreachable");
            }
           
       }
    }
}
