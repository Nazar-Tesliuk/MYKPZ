package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;       
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Server app for a WeaponStore due to software architecture
 *
 */
public class App 
{
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    
     static ExecutorService executeIt = Executors.newFixedThreadPool(2);
     static int ID = 0;
     static String[] Buffers = new String[3];
     static boolean test = false;

    public App(boolean nottest) throws InterruptedException, IOException {
         
            ServerSocket server = new ServerSocket(1234);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Server initialization...");
            while (!server.isClosed()) {
                if(ID == 2)
                    break;
                
                Socket client = server.accept();

                executeIt.execute(new MonoThreadClientHandler(client, ++ID, Buffers));
                System.out.println("Покупця товару знайдено. " + Integer.toString(ID));
            }
            
            System.out.println("Server started");
            test = true;
            
            if(nottest)
                while(true)
                {
                    if (br.ready()) { 
                        String cmdFromCLI = br.readLine();
                        if (cmdFromCLI.contains("show ")) {
                            char showID = cmdFromCLI.charAt(cmdFromCLI.indexOf("show ") + 5);
                            System.out.println("Зараз створюється потік товару" + showID + " ...");
                            System.out.println("Потік ящиків зброї:");
                            System.out.println();
                            int show = Character.getNumericValue(showID);
                            for(int i = 0; i < 5; i++)
                            {
                                Thread.sleep(500);
                                readLock.lock();
                                try {
                                    System.out.println(Buffers[show]);
                                } finally {
                                    readLock.unlock();
                                }
                            }
                            System.out.println();

                        } else if (cmdFromCLI.contains("quit")) {
                            System.out.println("Main Server exit...");
                            server.close();
                            break;
                        }
                    }
                }
           executeIt.shutdownNow();
    }

    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        App app = new App(true);
    }
    
    public static boolean isTest() {
        return test;
    }
}
