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
import java.util.Scanner;
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
             
             Scanner in = new Scanner(System.in);
             System.out.println("Escolha o esquema de encriptacao e respetivo modo: \n 1 - RC4  \n 2 - AES/CBC/NoPadding \n 3 - AES/CBC/PKCS5Padding"
                     + "\n 4 - AES/CFB/CFB8/NoPadding, \n 5 - AES/CFB8/NoPadding, \n 6 - AES/CFB/NoPadding");
             
              int n = in.nextInt();
             // System.out.println("e é isto: "+n);
              String mode;
               switch (n)
               {
                case 1: mode = "RC4";
                    break;
                case 2: mode =  "AES/CBC/NoPadding";
                    break; 
                case 3: mode = "AES/CBC/PKCS5Padding";
                    break;
                case 4: mode = "AES/CFB/CFB8/NoPadding";
                    break;
                case 5: mode = "AES/CFB8/NoPadding";
                    break;
                case 6: mode = "AES/CFB/NoPadding";
                    break;
                default: mode = "Invalid mode";
                    break;
                }
               System.out.println("Modo: "+ mode);
              
            
            // System.out.println("e é isto: "+a);
            
             if(n==1)
             {
              SecretKeySpec sk = new SecretKeySpec(key, "RC4");

                //Inicia a cipher para encriptar
                Cipher cipher = Cipher.getInstance(mode);
                cipher.init(Cipher.DECRYPT_MODE, sk);


               // Create server socket
               ServerSocket ss = new ServerSocket(4569);
               
               Socket s = ss.accept();
               
               BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                   writer.write("RC4");
                   writer.flush();
                   writer.close();
                   System.out.println("Falei com o cliente");
                   
               s.close();
               
               ServerSocket ss_novo = new ServerSocket(4567);

               // Start upload counter
               int counter = 0;

               System.out.println("Server started ...");

               while(true) {
                   // Wait for client            
                   Socket s_novo = ss_novo.accept(); 
                   // Increment counter
             
                   System.out.println("Accepted connection "+counter+".");
                   counter++;

                   // Open file to write to
                   FileOutputStream fos = new FileOutputStream(args[0]+"/"+counter);
                 
                   // Get socket input stream
                   InputStream sis = s_novo.getInputStream();
                   //System.out.println("é isto que sai do InputStream");
                   //System.out.println(sis);
                 
                   // Get file 50 bytes at a time
                   byte[] buffer = new byte[50];

                   //encriptando
                 //there's no more data

                   int bytes_read = sis.read(buffer);
               
                   
                   while (bytes_read > 0) {
                      fos.write(cipher.update(buffer,0,bytes_read));
                      bytes_read = sis.read(buffer);
                   }

                   // Close socket
                   s_novo.close();
                   System.out.println("Closed connection.");

                   // Close file
                   cipher.doFinal();
                   fos.close();
                
              }
             }
             else if(n==2 || n==3 || n==4 || n==5 || n==6)
             {
               ServerSocket ss = new ServerSocket(4569);
               
               Socket s = ss.accept();
               
               BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                   writer.write(mode);
                   writer.flush();
              
                   writer.close();
                   System.out.println("Falei com o cliente");
                   
               s.close();
                 
                 SecretKeySpec sk = new SecretKeySpec(key, "AES");
                //Cipher Type Initialization
                Cipher cipher = Cipher.getInstance(mode);
               // Initialisation vector:
                
                
               byte[] iv = new byte[cipher.getBlockSize()];


               // Create server socket
               ServerSocket ss_novo = new ServerSocket(4567);

               // Start upload counter
               int counter = 0;
               System.out.println("Server started ...");

               while(true) {
                   // Wait for client            
                   Socket s_novo = ss_novo.accept();
                   // Increment counter
                   counter++;
                   System.out.println("Accepted connection "+counter+".");
                   // Open file to write to
                   FileOutputStream fos = new FileOutputStream(args[0]+"/"+counter);

                   // Get socket input stream
                   InputStream sis = s_novo.getInputStream();

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
                   s_novo.close();
                   System.out.println("Closed connection.");

                   // Close file
                   //cipher.doFinal();
                   fos.close();
                  }
             }
                       
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
}
