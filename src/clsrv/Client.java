package clsrv;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;


public class Client {
    static public void main(String[] args) {
        try {
            
            // Read key
             System.out.println("\nCreating key for AES CPHR server side\n");
             FileInputStream kfis = new FileInputStream("key");
             byte[] key = new byte[16]; 
             kfis.read(key); 
             kfis.close();
             
              Socket s = new Socket("127.0.0.1",4569);
            
            System.out.println("Connected to server...");
            
            BufferedReader stdIn=new BufferedReader (new InputStreamReader( s.getInputStream()));
            System.out.println("Ouvi o servidor");
            System.out.println("Response from server:");
            String userInput;
          
            userInput = stdIn.readLine();
         
            System.out.println("Input:" + userInput); 
           // System.out.println("Input2:" + userInput2); 
             
            if(userInput.equals("RC4"))
            {
                Cipher cipher = Cipher.getInstance(userInput);
                SecretKeySpec sk = new SecretKeySpec(key, userInput);

                //Inicia a cipher para encriptar

                cipher.init(Cipher.ENCRYPT_MODE, sk);

                Socket s_novo = new Socket("127.0.0.1",4567);      
                System.out.println("Connected to server...");
                 // Open file to upload
                FileInputStream fis = new FileInputStream(args[0]);
                System.out.println("Abre ficheiro:" + fis);
                // Get socket output stream
                OutputStream sos = s_novo.getOutputStream();


                // Upload file 100 bytes at a time
                byte[] buffer = new byte[100];
                int bytes_read = fis.read(buffer);

                while (bytes_read == 100) {
                    sos.write(cipher.update(buffer));
                    bytes_read = fis.read(buffer);
                }


                sos.write(cipher.doFinal(buffer,0,bytes_read));

                System.out.println("Disconnected from server.");

                // Close socket
                sos.close();

                // Close file
                fis.close();
            }
            else
            {
             SecretKeySpec sk = new SecretKeySpec(key, "AES");
             
             //Inicia a cipher para encriptar
             Cipher cipher = Cipher.getInstance(userInput);
             
             // Initialisation vector:
            byte[] iv = new byte[cipher.getBlockSize()];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(iv); // If storing separately
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            
          
            
            // Connect to server
            Socket s_novo = new Socket("127.0.0.1",4567);
            
            System.out.println("Connected to server...");

            // Open file to upload
           FileInputStream fis = new FileInputStream(args[0]);

           // Get socket output stream
           OutputStream sos = s_novo.getOutputStream();
           
           // Sending IV to server
           sos.write(iv); 
           sos.flush();
           
           cipher.init(Cipher.ENCRYPT_MODE, sk, ivParameterSpec);
           
           
           // Upload file 100 bytes at a time
           byte[] buffer = new byte[100];
           int bytes_read = fis.read(buffer);
           while (bytes_read == 100) {
               sos.write(cipher.update(buffer));
               bytes_read = fis.read(buffer);
           }
           
           
           sos.write(cipher.doFinal(buffer,0,bytes_read));

           System.out.println("Disconnected from server.");
           
           // Close socket
           sos.close();
           
           // Close file
           fis.close(); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
