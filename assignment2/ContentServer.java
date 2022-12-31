import java.io.*;
import java.net.*;
import java.util.*;

public class ContentServer{
    private Socket cs;
    private static BufferedWriter out;
    private static BufferedReader in;
    private static int port;
    private static int LamportClock;

    public int start(String ip, int port) {
        try {
            cs = new Socket(ip, port);
            out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream()),100000);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            return 1;
        } catch (IOException e) {
            System.err.println("Client Server Start Exception: " + e.toString());
            e.printStackTrace();
            return 0;
        }

    }

    public void stop() {
        try {
            in.close();
            out.close();
            cs.close();
        } catch (IOException e) {
            System.err.println("Client Stop Exception: " + e.toString());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException{
        LamportClock = 0;
        ContentServer c = new ContentServer();
        Scanner sc = new Scanner(System.in);
        String input = "";
        final long pid = ProcessHandle.current().pid();
        boolean connected = false;
        // allows user to retry connection if aggregation server cannot be connected to
        while (!connected){
            System.out.println("CONNECT/EXIT");
            input = "";
            if (sc.hasNextLine()){
                input = sc.nextLine();
            }   else{ // for EOF only
                System.out.println("Exiting Content Server.");
                in.close();
                out.close();
                c.stop();
                sc.close();
                return;
            }

            if (new String("CONNECT").equals(input))
            {
                System.out.println("Choose a port (leave empty for default 4567): ");
                input = sc.nextLine();
                try {
                    port = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    port = 4567; // default port
                }
                int temp = c.start("localhost",port);
                if (temp == 1){
                    connected = true;
                }
            }
            if (new String("EXIT").equals(input))
            {
                System.out.println("Exiting Content Server.");
                in.close();
                out.close();
                c.stop();
                sc.close();
                return;
            }
        }

        boolean done = false;
        
        // heartbeat mechanism
        // new Thread(() -> {
        //     while(true){
        //         try {
        //             Thread.sleep(5000);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         try {
        //             out.write("HEARTBEAT");
        //             out.newLine();
        //             out.write(Integer.toString(LamportClock));
        //             out.newLine();
        //             out.write(Long.toString(pid));
        //             out.newLine();
        //             out.flush();
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //     } 
        // }).start();

        while(!done){
            System.out.println("PUT/EXIT");
            input = "";
            if (sc.hasNextLine()){
                input = sc.nextLine();
            }   else{
                // Thread.sleep(5000);
                System.out.println("Exiting Content Server.");
                in.close();
                out.close();
                c.stop();
                sc.close();
                done = true;
                return;
            }

            if (new String("EXIT").equals(input))
            {
                System.out.println("Exiting Content Server.");
                in.close();
                out.close();
                c.stop();
                sc.close();
                done = true;
                return;
            }

            if (new String("PUT").equals(input))
            {
                System.out.println("'END INPUT' to stop");
                LamportClock = LamportClock + 1;
                out.write(input);
                out.newLine();
                out.write(Integer.toString(LamportClock));
                out.newLine();
                out.write(Long.toString(pid));
                out.newLine();
                out.flush();
                String feedInput = "";
                // print response from AS
                while (sc.hasNextLine()){
                    feedInput = sc.nextLine();
                    if (new String("END INPUT").equals(feedInput)){
                        out.flush();
                        break;
                    }
                    out.write(feedInput);
                    out.newLine();
                    out.flush();
                }
                int receiveLC = Integer.parseInt(in.readLine());
                LamportClock = Math.max(receiveLC,LamportClock);
                System.out.println("local LC: " + LamportClock);
                in.close();
                out.close();
                c.start("localhost",port); // auto reconnect
            }
        }
        sc.close();
    }
}