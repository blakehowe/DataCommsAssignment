import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.*;
/**
 * Write a description of class Server here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 * Message Reciever
 */
public class Server extends Thread
{
    
    private DatagramSocket _udpSocket;
    private Client _client;
    private String _serverAuthCode;
    private ChatUserPool _userPool;
    
    public Server(DatagramSocket socket, Client client, String auth, ChatUserPool userPool) {
        _udpSocket = socket;
        _client = client;
        _serverAuthCode = auth;
        _userPool = userPool;
    }
    
    public void run() {
        //holds the message data from client.
        byte[] recievedBytes = new byte[1024];
        
        DatagramPacket packet;
        String messageString;
        
        while(true) {
            
            
            //wait until client thread is alive before continuing
            while (!_client.isAlive()) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    //thread has been interrupted
                    System.out.println("Warning: Unexpected behaviour has occurred running the server.");
                }
                finally {
                    System.out.println("\n--- Waiting for client process to start --- \n");
                }
            }
            
            
            
            packet = new DatagramPacket(recievedBytes, recievedBytes.length);
            
            try {
                //set socket timeout to infinity
                _udpSocket.setSoTimeout(0);
                _udpSocket.receive(packet);
                
                //get the IP and Port
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                
                //only print if not recieved from same ip
                if (!address.getHostAddress().equals(CommonFunctions.getIPAddress())) {
                    messageString = new String(packet.getData());
                
                    messageString = messageString.trim();
                    
                    //handle recieved message accordingly
                    if (messageString.startsWith("dmsg:")) {
                        //check if in verified users
                        
                        //remove prefix
                        messageString = messageString.replace("dmsg:", "");
                        
                        if (messageString.equals("null")) {
                            //is recieved when the socket closes on the clients end without notice?
                        }
                        else {
                            //chat message
                            System.out.println("\n\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" direct messaged: " + messageString);
                        }
                        _client.promptUserInput();
                        
                    }
                    else if (messageString.startsWith("msg:")) {
                        //check if in verified users
                        
                        //remove prefix
                        messageString = messageString.replace("msg:", "");
                        
                        if (messageString.equals("null")) {
                            //is recieved when the socket closes on the clients end without notice?
                        }
                        else {
                            //chat message
                            System.out.println("\n\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" says: " + messageString);
                        }
                        _client.promptUserInput();
                        
                    }
                    else if (messageString.equals("<LEFTCHAT>")) {
                        //peer left the chat
                        System.out.println("\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" left the chat room");
                        
                        //remove from peer pool
                        _userPool.removeAuthenticatedUser(address.getHostAddress());
                        
                        _client.promptUserInput();
                    }
                    else if (messageString.equals("<BYE>")) {
                        //the user has said goodbye
                        //peer is saying goodbye
                        System.out.println("\n\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" was nice enough to say goodbye before leaving");
                    }
                    else if (messageString.startsWith("authenticate:")) {
                        //user is attempting to authenticate with this chat server 
                        //System.out.println("\n\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" attempting to authenticate");
                        
                        //check if they passed the right authentication key
                        //respond to the user to let them know if they passed or failed authentication.
                        if (messageString.equals("authenticate:"+_serverAuthCode)) {
                            //the user has passed authentication
                            System.out.println("\n\n"+address.getHostName()+" ("+address.getHostAddress()+")"+" has joined the chat room");
                            
                            //add the user to this servers verified hosts list, however make sure they are not already in the verified users first?
                            if (!_userPool.addAuthenticatedUser(address.getHostAddress())) {
                                //user was already in valid hosts
                            }
                            
                            sendAuthResponse(address.getHostAddress(), 1);
                        }
                        else {
                            //the user attempted to authenticate but failed to pass the correct auth key
                            //using message authenticateResponse:false
                            
                            sendAuthResponse(address.getHostAddress(), 0);
                        }
                        _client.promptUserInput();
                    }
                    else {
                        //a string has been recieved that is not recognised therefore cannot be handled correctly.
                        
                        //System.out.println("\nUnrecognisable message recieved\n");
                    }
                }
                //clear recievedBytes
                recievedBytes = new byte[1024];
            }
            catch (SocketException e) {
                //socket was closed while waiting to recieve
                //break while loop
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
    }
    
    private void sendAuthResponse(String host, int response) {
        //response == 1 - true
        //response == 0 - false
        
        String message = "authenticateResponse:" + response;
        
        //convert the message string into bytes to be transferred with the datagram.
        byte[] messageBytes = message.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(host), _udpSocket.getLocalPort());
            
            _udpSocket.send(packet);
        } catch (Exception e) {
            System.err.println("Error: Unable to send authentication response to - " + host + ":" + _udpSocket.getLocalPort());
        }
    }
}
