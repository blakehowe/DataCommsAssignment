
/**
 * /**
 * A custom exception for the cryptography of a file.
 * @author www.codejava.net
 * @see http://www.codejava.net/coding/file-encryption-and-decryption-simple-example
 */

public class FileCryptException extends Exception {
 
    public FileCryptException() {
        
    }
 
    public FileCryptException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
