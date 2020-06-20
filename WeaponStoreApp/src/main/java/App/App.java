package App;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

enum STAGE
{
    WAIT,
    ASK,
    STREAM,
    END
}

/**
 * Client app for a WeaponStore
 *
 */
public class App 
{
    private static STAGE currentStage = STAGE.WAIT;
    
    /**
     * 
     * @param args
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {

        	Socket socket = new Socket("localhost", 1234);  
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
            DataInputStream ois = new DataInputStream(socket.getInputStream()); 
            
            System.out.println("Client connected to socket.");
            System.out.println();
                        
                String entry = "";         
                while(!socket.isOutputShutdown()){
                        if(currentStage == STAGE.WAIT) {
                            oos.writeUTF("client:ask:");
                            oos.flush();
                            currentStage = STAGE.ASK;
                            
                            entry = ois.readUTF();
                            
                        } else if(entry.contains("server:rdy:") && currentStage == STAGE.ASK){
                              oos.writeUTF("client:start:");
                              oos.flush();
                              currentStage = STAGE.STREAM;

                            
                              System.out.println("Закуповую товар: ");
                              for(int i = 0; i < 20; i++) 
                              {
                                  Thread.sleep(3000);
                                  System.out.println("Ящик зі зброєю №" + Integer.toString(i));
                                  oos.writeUTF("Ящик зі зброєю №" + Integer.toString(i));
                                  oos.flush();
                              }
                              
                              currentStage = STAGE.END;
                              oos.writeUTF("client:stop:");
                              break;
                              
                         }
                        else{
                        }    
                     }
            System.out.println("Closing connections & channels on clentSide - DONE.");
    }
}
