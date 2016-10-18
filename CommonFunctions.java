import java.net.*;
import java.io.*;
import java.util.*;
import java.net.InetAddress;
/**
 * Write a description of class CommonFunctions here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CommonFunctions
{
    public static String getIPAddress() {
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
    
    public static String getSubnetMask() {
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
    
    public static int getHostNumber() {
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
}
