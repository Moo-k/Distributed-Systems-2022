import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class SorterImplementation implements Sorter{
    private Stack<Integer> stack;


    public SorterImplementation() throws RemoteException
    {
        this.stack = new Stack<Integer>();
    }

    // push int val onto the stack, no output
    public void pushValue(int val){
        stack.push(val);
    }

    // ascending: smallest value on top of stack | descending: largest value on top of stack | max: push only max value | min: push only min value, no output
    // max & min: if multiple values are max/min, push them all
    public void pushOperator(String operator){
        Vector<Integer> vec = new Vector<>();
        while (!stack.empty()){
            vec.add(stack.pop());
        }
        Collections.sort(vec);
        if (new String("ascending").equals(operator)){
            for (int i = vec.size()-1 ; i >= 0 ; i--)
            {
                stack.push(vec.get(i));
            }
        } else if (new String("descending").equals(operator)){
            for (int i = 0; i < vec.size() ; i++)
            {
                stack.push(vec.get(i));
            }
        } else if (new String("max").equals(operator)){
            int max = vec.lastElement();
            int numMax = 0;
            for (int i = vec.size()-1; i >= 0; i--){
                if (vec.get(i).equals(max)){
                    numMax++;
                }   else{
                    break;
                }
            }

            for (int i = 0; i < numMax; i++){
                stack.push(max);
            }
        } else if (new String("min").equals(operator)){
            int min = vec.firstElement();
            int numMin = 0;
            for (int i = 0; i < vec.size(); i++){
                if (vec.get(i).equals(min)){
                    numMin++;
                }   else{
                    break;
                }
            }

            for (int i = 0; i < numMin; i++){
                stack.push(min);
            }
        }
    }

    // pop value on top of stack, returns int
    public int pop(){
        return stack.pop();
    }

    // check if the stack is empty, returns bool
    public boolean isEmpty(){
        return stack.empty();
    }

    // pop value on top of stack after x ms, returns int
    public int delayPop(int millis){
        try
        {
            Thread.sleep(millis);
        } catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        return stack.pop();
    }

    public void delay(int millis){
        try
        {
            Thread.sleep(millis);
        } catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}
