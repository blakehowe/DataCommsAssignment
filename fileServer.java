import java.net.*;     
import java.io.*;
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */     
     
public class FileServer extends Thread {  

    private int _tcpPort;
    private ChatUserPool _userPool;
    
    public FileServer(int tcpPort, ChatUserPool userPool) {
        _tcpPort = tcpPort;
        _userPool = userPool;
    }

    public void run() {
        //execute the ReceiveFile method asynchronously.
        try {
            ServerSocket socket = new ServerSocket(_tcpPort);
            while (true) {
                //blocks until a new connection is accepted from server socket.
                Socket client = socket.accept();
                if (_userPool.isAuthenticatedUser(client.getInetAddress().getHostAddress())) {
                    //new thread object.
                    ReceiveFile receiver = new ReceiveFile(client);
                    
                    //start downloading the file asychronously
                    receiver.start();
                }
            }
        }
        catch(IOException e){
            System.err.println("\n\nAn error occured starting the file receiver service, you may be unable to receive files from chat clients\n");
        }
    }
}