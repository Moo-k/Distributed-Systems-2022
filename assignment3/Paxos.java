import java.io.*;
import java.net.*;
import java.util.*;

public class Paxos{
    private ServerSocket ss;
    private static int behavior;
    private static int numClients;
    private static int want;
    private static int port;
    private static int max_id;
    private static boolean proposal_accepted;
    private static int accepted_ID;
    private static int accepted_value;
    private static int majority;

    public void start(int port) {
        try {
            ss = new ServerSocket(port);
            while (true){
                new ClientThread(ss.accept()).start();
                System.out.println("Connected");
            }
        } catch (IOException e) {
            System.err.println("Paxos Start Exception: " + e.toString());
            e.printStackTrace();
        } finally {
            this.stop();
        }
    }

    public void stop() {
        try {
            ss.close();
        } catch (IOException e) {
            System.err.println("Paxos Stop Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static class ClientThread extends Thread {
        private Socket cs;
        private BufferedWriter out;
        private BufferedReader in;

        public ClientThread(Socket socket) {
            this.cs = socket;
        }

        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream()),100000);

                String line = "";
                while ((line = in.readLine()) != null) {
                    // phase 1b (PROMISE)
                    if ((new String("PREPARE").equals(line))){
                        // String ids = in.readLine();
                        // System.out.println(ids);
                        int id = Integer.parseInt(in.readLine());
                        if (id <= max_id){
                            // ignore
                        }   else{
                            max_id = id;
                            if (proposal_accepted){
                                out.write("PROMISE");
                                out.newLine();
                                out.write(Integer.toString(accepted_ID));
                                out.newLine();
                                out.write(Integer.toString(accepted_value));
                                out.newLine();
                                out.flush();
                            }   else{
                                out.write("PROMISE");
                                out.newLine();
                                out.write("-2");
                                out.newLine();
                                out.write("-2");
                                out.newLine();
                                out.flush();
                            }
                        }
                    }
                    // phase 2b (ACCEPT)
                    else if ((new String("PROPOSE").equals(line))){
                        int id = Integer.parseInt(in.readLine());
                        int val = Integer.parseInt(in.readLine());
                        if (id == max_id){
                            proposal_accepted = true;
                            accepted_ID = id;
                            accepted_value = val;
                            // no learners in this implementation
                            System.out.println("ACCEPTED ID: " + accepted_ID + ", ACCEPTED VALUE: " + accepted_value);
                            out.write("ACCEPTED");
                            out.newLine();
                            out.write(accepted_ID);
                            out.newLine();
                            out.write(accepted_value);
                            out.newLine();
                            out.flush();
                        }   else{
                            System.out.println("Proposal Failed, ID: " + id);
                        }
                    }
                }

                in.close();
                out.close();
                cs.close();
            } catch (IOException e) {
                // whenever connection is closed
                // System.err.println("Server Exception: " + e.toString());
                // e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException{
        Paxos paxos = new Paxos();
        behavior = Integer.parseInt(args[0]);
        numClients = Integer.parseInt(args[1]);
        want = Integer.parseInt(args[2]);
        port = Integer.parseInt(args[3])+10000;
        int id = -1;
        max_id = -1;
        proposal_accepted = false;
        majority = (numClients+1)/2;
        int promises = 0;
        int highestacceptedid = -1;
        int highestacceptedvalue = -1;
        int val = -1;

        // proposer
        if (want == 1){
            try (BufferedReader br = new BufferedReader(new FileReader("id.txt"))) {
                id = Integer.parseInt(br.readLine());
                File oldid = new File("id.txt");
                oldid.delete();
                String path = "id.txt";
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.println(id+1);
                writer.close();
            } catch (IOException e){
                System.err.println("Prepare Phase 1a Proposer Exception: " + e.toString());
                e.printStackTrace();
            }
            Vector<Integer> vec = new Vector<Integer>();  
            for (int i = 10000; i<10000+numClients; i++){
                if (i != port){
                    vec.add(i);
                }
            }

            while (vec.size() > 0){
                for (int i = 0; i < vec.size(); i++){
                    Socket s;
                    BufferedWriter outPropose;
                    BufferedReader inPropose;
                    try{
                        System.out.println("ID: " + port + " trying to connect to port " + vec.get(i));
                        s = new Socket("localhost", vec.get(i));
                        outPropose = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()),100000);
                        inPropose = new BufferedReader(new InputStreamReader(s.getInputStream()));

                        // send PREPARE message to all other nodes
                        // phase 1a (PREPARE)
                        outPropose.write("PREPARE");
                        outPropose.newLine();
                        outPropose.flush();
                        outPropose.write(Integer.toString(id));
                        outPropose.newLine();
                        outPropose.flush();
                        System.out.println("ID: " + port + " Sent PREPARE Message to port " + vec.get(i));

                        // M2 behavior: 50% chance to respond instantly, 50% chance to delay 5000ms
                        if (behavior == 2){
                            Random rand = new Random();
                            int int_random = rand.nextInt(2);
                            System.out.println(int_random);
                            if (int_random == 1){
                                Thread.sleep(5000);
                            }
                        }
                        // M3 behavior: propose then go offline
                        if (behavior == 3){
                            System.exit(0);
                        }
                        // immediate response: behavior 1
                        // medium delay
                        if (behavior == 4){
                            Thread.sleep(2000);
                        }
                        // late delay
                        if (behavior == 5){
                            Thread.sleep(5000);
                        }

                        String reply = "";
                        while ((reply = inPropose.readLine()) != null){
                            // phase 2a (PROPOSE)
                            if ((new String("PROMISE").equals(reply))){
                                promises = promises + 1;
                                if (promises >= majority){
                                    System.out.println("Promise received, majority reached");
                                    if (highestacceptedid > 0){
                                        val = highestacceptedvalue;
                                    }   else{
                                        val = port-10000; // own node number
                                    }
                                    // send PROPOSE to all acceptors (and learners)
                                    outPropose.write("PROPOSE");
                                    outPropose.newLine();
                                    outPropose.write(Integer.toString(id));
                                    outPropose.newLine();
                                    outPropose.write(Integer.toString(val));
                                    outPropose.newLine();
                                    outPropose.flush();
                                }   else{
                                    System.out.println("Promise received, majority not reached");
                                }
                                // if promise comes with accepted value
                                if ((reply = inPropose.readLine()) != null){
                                    System.out.println("Phase 2a has accepted value");
                                    if (highestacceptedid >= Integer.parseInt(reply)){
                                        // ignore
                                    }   else{
                                        highestacceptedid = Integer.parseInt(reply);
                                        highestacceptedvalue = Integer.parseInt(inPropose.readLine());
                                    }
                                }
                            }
                            s.close();
                            outPropose.close();
                            inPropose.close();
                        }
                        System.out.println("4");
                        // close connection
                        inPropose.close();
                        outPropose.close();
                        s.close();
                        // sent = true;
                        System.out.println("Port "+ vec.get(i) + " removed");
                        vec.remove(i);
                        
                    } catch (IOException e){
                        // System.err.println("Prepare Phase 1a Proposer Write Exception: " + e.toString() + "ID: " + port);
                        // e.printStackTrace();
                    }
                }
            }
        }
        paxos.start(port);
    }
}