import java.net.*;  
import java.io.*;  
/**
 * Write a description of class Client here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
   
public class FileSender extends Thread {  
    
    private static int _timeout = 3000;
    private String _ip;
    private int _tcpPort;
    private String _filename;

    public FileSender(String ip, int tcpPort, String filename) {
        _ip = ip;
        _tcpPort = tcpPort;
        _filename = filename;
    }

    public void run () {
        SendFile();
    }
    
    private static Boolean doesFileExist(String fileName) {
        //http://alvinalexander.com/java/java-file-exists-directory-exists
        File file = new File(fileName);
        return file.exists();
    }
    
    
    public void SendFile () {
        //http://www.java2s.com/Code/Java/Network-Protocol/StringbasedcommunicationbetweenSocket.htm
        //https://coderanch.com/t/556838/java/java/Transferring-file-file-data-socket
        //http://stackoverflow.com/questions/4969760/set-timeout-for-socket
        
        //check whether the file exists or not
        if (!doesFileExist(_filename)) {
            System.out.println("Cannot file transfer to host: " + _ip + "; unable to locate file");
            return;
        }
        
        try {
                //open the socket to the client
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(_ip, _tcpPort), _timeout);
                
                //create the directory, as it may not exist.
                File dir = new File("EncryptedFiles");
                dir.mkdir();
            
                System.out.println("\n\nSending file: " + _filename + " to " + _ip + "\n");
                Client.promptUserInput();
                //send the filename to the client
                //can get name from the file object?

                //send the file requested to the client
                
                
                File toEncrypt = new File(_filename);
                File fileData = new File("EncryptedFiles/"+_filename+".encrypted");
                
                try {
                    //encrypt the file.
                    FileCryptography.encrypt(CommonFunctions.getCryptKey(), toEncrypt, fileData);
                } catch (FileCryptException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                
                byte[] databytearray = new byte[(int) fileData.length()];

                FileInputStream fis = new FileInputStream(fileData);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(databytearray, 0, databytearray.length);
                
                //outputstream that goes to the client
                DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
                
                //give the filename to client
                toClient.writeUTF(_filename);
                
                //write the data to the output stream
                toClient.write(databytearray, 0, databytearray.length);
                
                //flush data on the stream to client
                toClient.flush();   
                
                System.out.println("\n\n" + _ip + " has been sent: " + _filename + "\n");
                Client.promptUserInput();
                
                //close the socket.
                socket.close();
                
                //delete encrypted file
                fileData.delete();
        }
        catch (SocketTimeoutException ex)
        {
            //the connection timed out.
            System.err.println("\n\nFile transfer connection to " + _ip + " has timed out\n");
            Client.promptUserInput();
        }
        catch (IOException ex)
        {
            //unknown error with connection to client
            System.err.println("\n\nUnknown error transferring file to " + _ip + "\n");
            Client.promptUserInput();
        }
    }
}