package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


final class createTestClient implements Runnable
{
    createTestClient() 
    {
    }

public void run() 
    {
         try
         {
        	 Socket socket = new Socket("localhost", 1234);  
         
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
            DataInputStream ois = new DataInputStream(socket.getInputStream());
        STAGE currentStage = STAGE.WAIT;          
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
                          for(int i = 0; i < 30; i++)
                          {
                              System.out.println("Ящик зброї №" + Integer.toString(i));
                              Thread.sleep(20);
                              oos.writeUTF("Ящик зброї №" + Integer.toString(i));
                              oos.flush();
                          }
                          currentStage = STAGE.END;
                          oos.writeUTF("client:stop:");
                          break;  
                     }
                 }
            } catch (IOException | InterruptedException ex) {
            Logger.getLogger(createTestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	static ExecutorService executeIt = Executors.newFixedThreadPool(3);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    /**
     * Test of main method, of class App.
     * @throws java.lang.Exception
     */
    public void testServer() throws Exception {
        
        System.out.println();
        System.out.println();
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println();
        System.out.println("TEST WILL TAKE 30 sec");
        System.out.println("PLEASE WAIT!");
        System.out.println();
        System.out.println("---------------------------------------------------------------------------------------------");

        // act
        // run clients
        executeIt.execute(new createTestClient());
        executeIt.execute(new createTestClient());
        // run server
        App app = new App(false);
        executeIt.shutdownNow();
        assertEquals(true, App.isTest());
    }
}
