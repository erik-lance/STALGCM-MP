import java.util.ArrayList;
import java.io.BufferedWriter;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class View {
    private static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

    /**
     * Prints "equivalent" if two NFAs are equal, otherwise prints "not equivalent".
     * @param bIsEqual boolean value which determines if two NFAs are equal
     */
    public static void phase1Print(boolean bIsEqual) {
        try {
            String output = (bIsEqual == true) ? "equivalent\n" : "not equivalent\n";
            out.write(output);
            out.flush();
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
            Collections.sort(arrEquivalent, new Comparator<Machine>() {
                @Override
                public int compare(Machine m1, Machine m2) {
                    return m1.getName().compareToIgnoreCase(m2.getName());
                }
            });
            for (Machine m : arrEquivalent) out.write(m.getName() + " ");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}