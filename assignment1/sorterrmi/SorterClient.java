import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.util.*;
import java.util.Scanner;

public class SorterClient {

    private SorterClient() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Sorter stub = (Sorter) registry.lookup("Sorter");
            boolean done = false;
            Scanner sc = new Scanner(System.in);
            while (!done)
            {
                // Scanner sc = new Scanner(System.in);
                System.out.println("Commands: pushValue/pushOperator/pop/isEmpty/delayPop/close/delay");
                String command = "";
                if (sc.hasNextLine()){
                    command = sc.nextLine();
                }   else{
                    System.out.println("EOF");
                    break;
                }

                // close the client
                if (new String("close").equals(command))
                {
                    System.out.println("Exiting Client.");
                    done = true;
                }

                // ascending: smallest value on top of stack | descending: largest value on top of stack | max: push only max value | min: push only min value
                else if (new String("pushOperator").equals(command))
                {
                    System.out.println("Enter operator: ascending/descending/max/min");
                    String operator = sc.nextLine();
                    stub.pushOperator(operator);
                    System.out.println("Stack rearranged using: " + operator);
                }

                // push value onto the stack
                else if (new String("pushValue").equals(command))
                {
                    System.out.println("Enter value:");
                    int input = sc.nextInt();
                    sc.nextLine();
                    stub.pushValue(input);
                    System.out.println("Value: " + input + " pushed.");
                }

                // pop after x ms and print value
                else if (new String("delayPop").equals(command))
                {
                    System.out.println("Enter delay:");
                    int input = sc.nextInt();
                    sc.nextLine();
                    int value = stub.delayPop(input);
                    System.out.println("Value: " + value + " popped after " + input + "ms.");
                }

                // pop and print value
                else if (new String("pop").equals(command))
                {
                    int value = stub.pop();
                    System.out.println("Value: " + value + " popped.");
                }

                // isEmpty command
                else if (new String("isEmpty").equals(command))
                {
                    if (stub.isEmpty())
                    {
                        System.out.println("The stack is empty.");
                    } else
                    {
                        System.out.println("The stack is not empty.");
                    }
                }

                else if (new String("delay").equals(command))
                {
                    System.out.println("Enter delay:");
                    int input = sc.nextInt();
                    sc.nextLine();
                    stub.delay(input);
                    System.out.println("Waited for " + input + " ms.");
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
