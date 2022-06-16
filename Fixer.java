import java.util.ArrayList;

public class Fixer {
    private String name;
    private ArrayList<State> states;

    public Machine convertToDFA(Machine m) {
        return null;
    }


    /**
     * This will submit the machine to a reduction algorithm. Will return
     * a completely new machine with the same name.
     * @param m is the machine to perform the algorithm on
     * @return a new reduced and connected machine with the same name
     */
    public Machine partitionAlgorithm(Machine m) {

        // Check if DFA before submitting to algo

        // This is an array list of an array list of an array list of partitions. This will arrange them
        // based on their "partition" number.

        // E.g. : Partition 0, Group 0, State 0
        ArrayList<ArrayList<ArrayList<State>>> partitions = new ArrayList<ArrayList<ArrayList<State>>>();


        // Initial Partitioning

        // Get all End States
        for (State mState : m.getStates()) {
            int i = 0;
            if (mState.isBFinal())
            {
                partitions.get(0).get(0).add(mState);
                i++;
            }

            // No final states, therefore this machine is invalid.
            if (i == 0) return null;
        }

        // Get all non-end states. (Can't be subjected to else state above in case there is no end state yet)
        for (State mState : m.getStates()) {
            if (!mState.isBFinal())
            {
                partitions.get(0).get(1).add(mState);
            }
        }

        
        /* ---------- Full Algorithm Section ---------- */

        // If number of partitions
        if (partitions.get(1).size() != m.getStates().size()) {
        }




        return null;
    }

    /**
     * This will attempt to reduce the machine once. Given a certain partition. It will return
     * a new partition that can be used for the partitions variable as seen in the partitionAlgorithm function
     * @param pGroup is the partition group being used as reference
     * @param inputs is the list of possible inputs that a machine will use.
     * @return an arraylist arraylist of states (which is the same datatype as the partitions variable in the partitionAlgorithm function)
     */
    public ArrayList<ArrayList<State>> reduceOnce(ArrayList<ArrayList<State>> pGroup, ArrayList<String> inputs) {

        ArrayList<String> transitionString = new ArrayList<String>();

        // Check each state and create a transition code for each.
        for (int i = 0; i < pGroup.size(); i++) {

            //Check each state in a pGroup
            for (int j = 0; j < pGroup.get(i).size(); j++) {
                transitionString.add("");
                // Check each transition possible (especially since DFA, each input must be possible)
                for (int k = 0; k < inputs.size(); k++) {
                    State start = pGroup.get(i).get(j);
                    State dest = pGroup.get(i).get(j).getTransitions().get(k).getDest();

                    // This feeds the current partition, the start state, and t he destination state.
                    String appenString = getDestGroup(pGroup, start, dest);
                    transitionString.set(j, transitionString.get(j)+appenString);
                }

            }

        }

        return null;
    }

    /**
     * Returns the string group of where the said state will go to
     * @param pGroup is the partition group to check
     * @param start is the starting state to look through
     * @param dest is the destination state where the starting state will end up in
     * @return the string number of the group number.
     */
    public String getDestGroup(ArrayList<ArrayList<State>>pGroup, State start, State dest) {
        
        // Loops through each state of each group
        for (ArrayList<State> stateGroup: pGroup) {
            int groupNum = 0;

            for (State state : stateGroup) {
                if (state == dest) {
                    return String.valueOf(groupNum);
                }
                
            }
            groupNum++;
        }

        return null;
    }
}
