import java.net.*;  
import java.io.*;  
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
   
public class FileSender extends Thread {  

    private Socket _tcpSocket;
    String file = "SnakeRiver";

    public FileSender(Socket socket) {
        _tcpSocket = socket;
    }
	
    public void run () {
        try {
   		   //Send file  
            File myFile = new File(file);  
            //creates an array of byte to store the file which will be sent in the data output stream.
            byte[] mybytearray = new byte[(int) myFile.length()];  
            FileInputStream fis = new FileInputStream(myFile);  
            BufferedInputStream bis = new BufferedInputStream(fis);    
            //creates a data input stream with the buffered input stream wrapped in it
            DataInputStream dis = new DataInputStream(bis);     
            dis.readFully(mybytearray, 0, mybytearray.length);  
           
            OutputStream os = _tcpSocket.getOutputStream();  
           
            //Sending file name, size and data to the server  
            DataOutputStream dos = new DataOutputStream(os);     
            dos.writeUTF(myFile.getName());     
            dos.writeLong(mybytearray.length);     
            dos.write(mybytearray, 0, mybytearray.length);     
            dos.flush();
            //Closes streams and socket.
            os.close();
            dos.close();  
            _tcpSocket.close(); 
        }  
        catch(Exception e)
        {
            System.out.println("Error sending file");
        } 
    }
}