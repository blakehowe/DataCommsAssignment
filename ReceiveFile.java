import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Write a description of class ReceiveFile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ReceiveFile extends Thread
{
    private Socket _clientSocket;
    
    public ReceiveFile(Socket clientSocket) {
        _clientSocket = clientSocket;
    }
    
    public void run() {
        //receive the file asynchronously.
        String hostName = _clientSocket.getInetAddress().getCanonicalHostName();
        String ipAddress = _clientSocket.getInetAddress().getHostAddress();
        try {
                int bytesRead; 
                
                //inputstream from the client server
                InputStream clientStream = _clientSocket.getInputStream();
                
                //get the filename
                DataInputStream clientData = new DataInputStream(clientStream);
                String fileName = clientData.readUTF();
                
                //create the directory, as it may not exist.
                File dir = new File("FilesReceived");
                dir.mkdir();
                
                //creates output stream and passes in command line argument 'fileName'    
                OutputStream fileStream = new FileOutputStream("FilesReceived/"+fileName+".encrypted");
                
                //read the data to the output stream
                byte[] buffer = new byte[1024];
                while ((bytesRead = clientStream.read(buffer)) != -1) {
                    fileStream.write(buffer, 0, bytesRead);
                }
                
                // Closing the FileOutputStream and Socket
                clientData.close();
                fileStream.close();
                
                //decrypt the file now that is received
                File toDecrypt = new File("FilesReceived/"+fileName+".encrypted");
                File decrypted = new File("FilesReceived/"+fileName);
                FileCryptography.decrypt("Vr itmud Hv zLXN", toDecrypt, decrypted);
                
                //delete encrypted file
                toDecrypt.delete();
                
                System.out.println("\n\n" + hostName + " (" + ipAddress + ") has sent you a file: " + fileName);
        }
        catch(Exception e) 
        {
            System.err.println(e.getMessage());
            System.out.println("Server unable to receive the file from: " + _clientSocket.getInetAddress().getCanonicalHostName());
        }
        
        Client.promptUserInput();
    }
}
