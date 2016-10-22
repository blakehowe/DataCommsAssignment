import java.net.*;     
import java.io.*;
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */     
     
public class FileServer extends Thread {  
    int port;
    int bytesRead; 

    public FileServer(ServerSocket socket) {
        _tcpServerSocket = socket;
    }
    
    public void run() {
        while(true) {  
            Socket clientSocket = null;  
            clientSocket = serverSocket.accept();  //checks for client connection and accepts it

            //send this socket to the other thread
           
            InputStream in = clientSocket.getInputStream();  
           
            DataInputStream clientData = new DataInputStream(in);   
           
            String fileName = clientData.readUTF();
            //creates output stream and passes in command line argument 'fileName'    
            OutputStream output = new FileOutputStream(fileName);     
            long size = clientData.readLong();
            //creates array of byte to send data to the server  
            byte[] buffer = new byte[1024];
            //checks that the byte size is more than 0. If it equals 0, end the write of the file.     
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)  
            {     
                output.write(buffer, 0, bytesRead);     
                size -= bytesRead;     
            }  
        
            System.out.println("Transfer successfully completed.");

            // Closing the FileOutputStream handle
            in.close();
            clientData.close();
            output.close();  
        }  
    }
} 