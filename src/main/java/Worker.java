import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Worker extends Thread{
    private final ConnectionPool pool;

    public Worker(ConnectionPool pool) {
        this.pool = pool;
    }

    public void run() {
        while (true) {
            try (
                    // Get connection from the pool
                    Socket client = pool.getConnectionRequest();

                    // Open up read and write streams
                    DataInputStream input = new DataInputStream(client.getInputStream());
                    DataOutputStream output = new DataOutputStream(client.getOutputStream());
            ) {

                // REF DO SOMETHING
                System.out.println("ts pmo icl fr fr");

            } catch (IOException ioe) {
                System.out.println("IOException: " + ioe.getMessage());
                pool.killConnection();
            } catch (RuntimeException e) {
                System.out.println("Runtime Exception: " + e.getMessage());
                pool.killConnection();
            } catch (InterruptedException ie) {
                System.out.println("Interruption Exception: " + ie.getMessage());
                pool.killConnection();
            }
        }
    }
}
