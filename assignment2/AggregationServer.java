import java.io.*;
import java.net.*;
import java.util.HashMap;

public class AggregationServer {
    private ServerSocket as;
    private static int LamportClock;
    private static HashMap<String, Integer> map = new HashMap<>(); // maps pid to LC

    public void start(int port) {
        try {
            LamportClock = 0;
            as = new ServerSocket(port);
            while (true){
                new ClientThread(as.accept()).start();
                System.out.println("Connected");
            }
        } catch (IOException e) {
            System.err.println("Aggregate Server Start Exception: " + e.toString());
            e.printStackTrace();
        } finally {
            this.stop();
        }
    }

    public void stop() {
        try {
            as.close();
        } catch (IOException e) {
            System.err.println("Aggregate Server Stop Exception: " + e.toString());
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
                    if ((new String("GET").equals(line))){
                        int receiveLC = Integer.parseInt(in.readLine());
                        LamportClock = Math.max(receiveLC,LamportClock+1);
                        System.out.println(line + " request from Client");
                        System.out.println("local LC: " + LamportClock);
                        out.write(Integer.toString(LamportClock));
                        out.newLine();
                        out.flush();
                        File dir = new File("./feed");
                        File[] fileList = dir.listFiles();
                        // for each file in /feed, print their contents to client's input stream
                        for (int i = 0; i < fileList.length; i++) {
                            try (BufferedReader br = new BufferedReader(new FileReader("./feed/"+fileList[i].getName()))) {
                                String nextline;
                                while ((nextline = br.readLine()) != null) {
                                    out.write(nextline);
                                    out.newLine();
                                    out.flush();
                                }
                            }   catch (IOException e){
                                System.err.println("Aggregate Server Feed Exception: " + e.toString());
                                e.printStackTrace();
                            }
                            out.flush();
                        }
                        System.out.println("200 - SUCCESS");
                        in.close();
                        out.close();
                        cs.close();
                    }
                    else if ((new String("PUT").equals(line))){
                        int receiveLC = Integer.parseInt(in.readLine());
                        LamportClock = Math.max(receiveLC,LamportClock+1);
                        String pid = in.readLine();
                        System.out.println(line + " request from CS with pid " + pid);
                        System.out.println("local LC: " + LamportClock);
                        out.write(Integer.toString(LamportClock));
                        out.newLine();
                        out.flush();
                        System.out.println("200 - SUCCESS");
                        if (map.containsKey(pid)) {
                            Integer oldLC = map.get(pid);
                            System.out.println("old LC value: " + oldLC);
                            File oldFile = new File("./feed/"+oldLC+".txt");
                            oldFile.delete();
                        }
                        map.put(pid,LamportClock);
                        String feedInput = "";
                        String wholeInput = "";
                        while ((feedInput = in.readLine()) != null){
                            wholeInput = wholeInput + feedInput + "\n";
                            System.out.println("200 - SUCCESS");
                        }
                        wholeInput = (wholeInput.substring(0, wholeInput.length() - 1));
                        if (new File("./feed").mkdir()){
                            System.out.println("201 - CREATED");
                        }
                        String path = "./feed/"+Integer.toString(LamportClock)+".txt";
                        PrintWriter writer = new PrintWriter(path, "UTF-8");
                        writer.println(wholeInput);
                        writer.close();
                    }
                    // heartbeat handler
                    // else if ((new String("HEARTBEAT").equals(line))){
                    //     // first line LC
                    //     int receiveLC = Integer.parseInt(in.readLine());
                    //     LamportClock = Math.max(receiveLC,LamportClock+1);
                    //     // second line pid
                    //     String pid = in.readLine();
                    //     System.out.println("local LC: " + LamportClock);
                    //     System.out.println(line + " request from CS with pid " + pid);
                    // }
                    else{
                        // anything other that GET or PUT received, but input is validated at Client/CS, not here
                        System.out.println("400 - BAD REQUEST");
                    }
                }

                in.close();
                out.close();
                cs.close();
            } catch (IOException e) {
                // whenever connection is closed
                // System.err.println("Aggregate Server Exception: " + e.toString());
                // e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        AggregationServer aggr = new AggregationServer();
        if (args.length > 0) {
            aggr.start(Integer.parseInt(args[0]));
        } else{
            aggr.start(4567);
        }
    }
}