package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
enum STAGE {
    WAIT,
    ASK,
    STREAM,
    END
}

/**
 * Mono server (helps to create MultiThreadServer in App.java
 *
 * @author
 */
public class MonoThreadClientHandler implements Runnable {

    private static Socket clientDialog;
    
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock writeLock = readWriteLock.writeLock();

    private static STAGE currentStage;
    private static int ID;
    private static String[] VideoBuffer;

    public MonoThreadClientHandler(Socket client, int ID, String[] Buffers) {
        MonoThreadClientHandler.clientDialog = client;
        currentStage = STAGE.WAIT;
        MonoThreadClientHandler.setID(ID);
        VideoBuffer = Buffers;
        VideoBuffer[ID] = "VideoCam is not ready...";
    }

    public void run() {

        try {
            try { 
            
                    DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream()); // канал чтения из сокета
                    DataInputStream in = new DataInputStream(clientDialog.getInputStream());
                
                while (!clientDialog.isClosed()) {
                    String entry = in.readUTF();
                    
                    if (entry.equalsIgnoreCase("client:ask:") && currentStage == STAGE.WAIT) {
                        out.writeUTF("server:rdy:");
                        currentStage = STAGE.ASK;
                    } else if (entry.equalsIgnoreCase("client:start:") && currentStage == STAGE.ASK) {
                        currentStage = STAGE.STREAM;
                    } else if (entry.equalsIgnoreCase("client:stop:") && currentStage == STAGE.STREAM) {
                        currentStage = STAGE.END;
                        out.writeUTF("Server:end:");
                        break;
                    }
                    else if(currentStage == STAGE.STREAM){
                        Thread.sleep(500);
                        writeLock.lock();
                        try {
                            MonoThreadClientHandler.VideoBuffer[ID] = entry;
                        } finally {
                            writeLock.unlock();
                        }
                    }
                    else{
                    } 
                    
                    out.flush();
                    
                }
                
                System.out.println("Робота з клієнтом [ID = " + Integer.toString(ID) + "] завершена");
                
            }
            finally
            {  	
            }
            
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
        } catch (InterruptedException ex) {
            Logger.getLogger(MonoThreadClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setID(int ID) {
        MonoThreadClientHandler.ID = ID;
    }
}
