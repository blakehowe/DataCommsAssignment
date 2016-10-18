import java.net.*;
import java.io.*;
import java.net.InetAddress;

/**
 * Write a description of class TCPClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TCPClient
{

    public static void main(String[] args) {
        checkHosts();
    }
    
    private static String getIPAddress() {
        String ip = null;
        
        try {
            InetAddress me = InetAddress.getLocalHost();
            ip = me.getHostAddress();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return ip;
    }
    
    private static String getSubnetMask() {
        // http://stackoverflow.com/questions/1221517/how-to-get-subnet-mask-of-local-system-using-java
        
        String subnet = null;
        
        try {
            String local=getIPAddress();
            String[] ip_component = local.split("\\.");
            subnet=ip_component[0]+"."+ip_component[1]+"."+ip_component[2];
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return subnet;
    }
    
    private static int getHostNumber() {
        int hostNum = 0;
        
        try {
            String local = InetAddress.getLocalHost().getHostAddress();
            String[] ip_component = local.split("\\.");
            hostNum = Integer.parseInt(ip_component[3]);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return hostNum;
    }

    
    public static void checkHosts(){
       String subnet = getSubnetMask();
       System.out.println(subnet);
       int hostNum = getHostNumber();
       System.out.println(hostNum);
       
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
       
       //calculate 10 before, 10 after.
       int timeout=20;
       for (int i = hostNumBefore; i < hostNumAfter + 1; i++){
           String host=subnet + "." + i;
           try {
               if (InetAddress.getByName(host).isReachable(timeout)){
               System.out.println(host + " is reachable");
            }
           } catch (Exception e) {
               System.out.println(host + " is unreachable");
            }
           
       }
    }
}

