import java.util.ArrayList;
import java.util.HashSet;

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

        // Loop that calls expandOnce until fully expanded.
        boolean dividable = true;
        ArrayList<ArrayList<State>> newPartition = new ArrayList<ArrayList<State>>();
        int numPartitions = 0;

        while (dividable) {
            newPartition = new ArrayList<ArrayList<State>>();
            ArrayList<ArrayList<State>> reduced = expandOnce(partitions.get(numPartitions), m.inputs);

            if (reduced != null) newPartition.addAll(reduced);
            else dividable = false;

            // Haven't utilized the partition function to number each partition, for now ganito muna.
        }

        
        /* ----- State Reduction ----- */

        ArrayList<ArrayList<State>> finalPartition = newPartition;
        Machine finalMachine = new Machine(m.getName(), 0);

        // Each group is considered a state. The individual states will be used as reference for now.
        int finalTransitionNum = 0;


        for (int i = 0; i < finalPartition.size(); i++) 
        {
            State newState = new State(finalPartition.get(i).get(0));
            finalMachine.getStates().add(newState);
        }

        int i = 0;
        for (State state : finalMachine.getStates()) 
        {
            int j = 0;
            for (Transition t : state.getTransitions()) 
            {
                State destTrans = finalPartition.get(i).get(0).getTransitions().get(j).getDest();
                String inputString = destTrans.getTransitions().get(j).getInput();
                int groupTransition = Integer.parseInt(getDestGroup(finalPartition, finalPartition.get(i).get(0), destTrans));

                // By getting the group transition number, we can easily feed the destination state based on the group number (w/c is the index num of the state)
                state.makeTransition(finalMachine.getStates().get(groupTransition),inputString);
                j++;
            }
            i++;    
        }


        return finalMachine;
    }

    /**
     * This will attempt to expand the machine once. Given a certain partition. It will return
     * a new partition that can be used for the partitions variable as seen in the partitionAlgorithm function
     * @param pGroup is the partition group being used as reference
     * @param inputs is the list of possible inputs that a machine will use.
     * @return an arraylist arraylist of states (which is the same datatype as the partitions variable in the partitionAlgorithm function)
     */
    public ArrayList<ArrayList<State>> expandOnce(ArrayList<ArrayList<State>> pGroup, ArrayList<String> inputs) {

        // A group of partitions of states based on reject/accept
        // E.g. Group 0, State 0
        ArrayList<ArrayList<String>> transitionStringAccept = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> transitionStringReject = new ArrayList<ArrayList<String>>();

        // Check each state and create a transition code for each.
        for (int i = 0; i < pGroup.size(); i++) 
        {
            // All finals should be in one group
            boolean isFinal = pGroup.get(i).get(0).isBFinal();

            if (isFinal) transitionStringAccept.add(new ArrayList<String>());
            else transitionStringReject.add(new ArrayList<String>());

            //Check each state in a pGroup
            for (int j = 0; j < pGroup.get(i).size(); j++) 
            {
                // Create initial string based on reject/accept
                if (isFinal) transitionStringAccept.get(i).add("");
                else transitionStringReject.get(i).add("");


                // Check each transition possible (especially since DFA, each input must be possible)
                for (int k = 0; k < inputs.size(); k++) 
                {
                    State start = pGroup.get(i).get(j);
                    State dest = pGroup.get(i).get(j).getTransitions().get(k).getDest();

                    // This feeds the current partition, the start state, and the destination state.
                    String appenString = getDestGroup(pGroup, start, dest);

                    if (isFinal) 
                    {
                        transitionStringAccept.get(i).set(j, transitionStringAccept.get(i).get(j)+appenString);
                    }
                    else 
                    {
                        transitionStringReject.get(i).set(j, transitionStringReject.get(i).get(j)+appenString);
                    }
                    
                }

            }
        }

        ArrayList<ArrayList<State>> reducedList = new ArrayList<ArrayList<State>>();

        // After checking all transitions, separate into partitions if needed starting with acceptors
        int numGroups = 0;
        for (int i = 0; i < transitionStringAccept.size(); i++) 
        {   
            // This makes it easier to collect only unique string transition values
            HashSet<String> hasher = new HashSet<String>();
            
            // Check each state
            for (String trans : transitionStringAccept.get(i)) {
                hasher.add(trans);
            }

            // Separate into a group based on number of unique values
            for (String hash : hasher) 
            {
                int k = 0;
                reducedList.add(new ArrayList<State>());
                for (String trans : transitionStringAccept.get(i)) 
                {
                    if (trans.equals(hash)) 
                    {
                        reducedList.get(numGroups).add(pGroup.get(i).get(k));
                    }
                    k++;
                }
                numGroups++;
            }
        }

        /* ---------- THIS IS THE SAME CODE BUT FOR REJECTORS ---------- */
        for (int i = 0; i < transitionStringReject.size(); i++) 
        {   
            // This makes it easier to collect only unique string transition values
            HashSet<String> hasher = new HashSet<String>();
            
            // Check each state
            for (String trans : transitionStringReject.get(i)) {
                hasher.add(trans);
            }

            // Separate into a group based on number of unique values
            for (String hash : hasher) 
            {
                int k = 0;
                reducedList.add(new ArrayList<State>());
                for (String trans : transitionStringReject.get(i)) 
                {
                    if (trans.equals(hash)) 
                    {
                        reducedList.get(numGroups).add(pGroup.get(i).get(k));
                    }
                    k++;
                }               
                numGroups++;
            }
        }
        

        // Returns null if list is found to be the same as it was. Indicating that this was the last partition.
        if (pGroup.equals(reducedList)) return null;
        else return reducedList;
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
