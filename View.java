import java.util.ArrayList;
import java.io.BufferedWriter;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class View {
    public static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

    /**
     * Prints "equivalent" if two NFAs are equal, otherwise prints "not equivalent".
     * @param bIsEqual boolean value which determines if two NFAs are equal
     */
    public static void phase1Print(boolean bIsEqual) {
        try {
            String output = (bIsEqual == true) ? "equivalent\n" : "not equivalent\n";
            out.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints number of clusters of equivalent machines and the names of the machines (lexicographically arranged)
     * separated by a single space.
     * @param arrEquivalent an ArrayList<Machine> which contains clusters of equivalent machines
     */
    public static void phase2Print(ArrayList<Machine> arrEquivalent) {
        try {
            out.write(arrEquivalent.size() + "\n");
            // lexicographically sorts arrEquivalent based on machine names
            Collections.sort(arrEquivalent, new Comparator<Machine>() {
                @Override
                public int compare(Machine m1, Machine m2) {
                    return m1.getName().compareToIgnoreCase(m2.getName());
                }
            });
            for (Machine m : arrEquivalent) out.write(m.getName() + " ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints a Machine instance for debugging purposes
     * @param m a Machine to be printed when debugging
     */
    public static void machinePrint(Machine m) {
        try {
            int i, j;
            State tempState;
            out.write("Machine Name: " + m.getName() + "\nInputs:\n");
            for (i = 0; i < m.getInputs().size(); i++) {
                out.write(m.getInputs().get(i) + "\n");
            }
            out.write("States:\n");
            for (i = 0; i < m.getStates().size(); i++) {
                out.write(m.getStates().get(i).name);
                tempState = m.getStates().get(i);
                out.write("Transitions Size: " + tempState.getTransitions().size() + "\nTransitions:\n");
                for (j = 0; j < tempState.getTransitions().size(); j++) {
                    out.write(tempState.getTransitions().get(i).toString());
                    //out.write(tempState.displayTransitions());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
