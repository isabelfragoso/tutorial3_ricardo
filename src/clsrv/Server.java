package clsrv;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;

public class Server {
    static public void main(String[] args) {
        try {           
            // Read key
             System.out.println("\nCreating key for AES CPHR server side\n");
             FileInputStream kfis = new FileInputStream("key");
             byte[] key = new byte[16]; 
             
             kfis.read(key); 
             kfis.close();
            
             SecretKeySpec sk = new SecretKeySpec(key, "AES");
             
             //Cipher Type Initialization
             Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
             
             
            // Initialisation vector:
            byte[] iv = new byte[cipher.getBlockSize()];
            
          
            // Create server socket
            ServerSocket ss = new ServerSocket(4567);
            
            // Start upload counter
            int counter = 0;
            
            System.out.println("Server started ...");

            while(true) {
                // Wait for client            
                Socket s = ss.accept();
                
                // Increment counter
                counter++;

                System.out.println("Accepted connection "+counter+".");

                // Open file to write to
                FileOutputStream fos = new FileOutputStream(args[0]+"/"+counter);

                // Get socket input stream
                InputStream sis = s.getInputStream();
                
                //Reading IV coming from client
                sis.read(iv);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, sk, ivParameterSpec);
                // Get file 50 bytes at a time
                byte[] buffer = new byte[50];
                
               
                 
                int bytes_read = sis.read(buffer);
                System.out.println(bytes_read);
                while (bytes_read > 0) {
                   fos.write(cipher.update(buffer,0,bytes_read));
                   bytes_read = sis.read(buffer);
                }
                  fos.write(cipher.doFinal()); //http://stackoverflow.com/questions/11065063/aes-cbc-pkcs5padding-issue
           
                // Close socket
                s.close();
                System.out.println("Closed connection.");
                                
                // Close file
                //cipher.doFinal();
                fos.close();
            }
                       
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
}
