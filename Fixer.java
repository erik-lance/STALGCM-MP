import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;

public class Fixer {
    private String name;
    private ArrayList<State> states;


    public boolean isEquivalent(Machine m1, Machine m2) {
        // Reference to states
        ArrayList<State> mStates1 = m1.getStates();
        ArrayList<State> mStates2 = m2.getStates();

        ArrayList<State> newStates = new ArrayList<State>();
        State initState1 = m1.getInitialState();
        State initState2 = m2.getInitialState();


        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","K","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int alphCounter = 0;

        // CREATES A STORE STATE
        ArrayList<State> storageStates = new ArrayList<State>();

        //Machine 1 Cloning
        for (State state : mStates1) {
            storageStates.add(new State(state));
        }

        // Deep Clones Transitions
        for (int i = 0; i < mStates1.size(); i++) 
        {
            for (Transition trSt : mStates1.get(i).getTransitions()) 
            {
                for (State storStat : storageStates) 
                {
                    if (trSt.getDest().equals(storStat)) 
                    {
                        storageStates.get(i).makeTransition(storStat, trSt.getInput());
                        break;
                    }
                }        
            }
        }

        //Machine 2 Cloning
        for (State state : mStates2) {
            storageStates.add(new State(state));
        }

        // Deep Clones Transitions
        for (int i = 0; i < mStates2.size(); i++) 
        {
            for (Transition trSt : mStates2.get(i).getTransitions()) 
            {
                for (State storStat : storageStates) 
                {
                    if (trSt.getDest().equals(storStat)) 
                    {
                        storageStates.get(i).makeTransition(storStat, trSt.getInput());
                        break;
                    }
                }        
            }
        }

        // ------ STATES COMPLETE; PARTITION STARTS HERE ------  //
        ArrayList<ArrayList<State>> partitionGroup = new ArrayList<ArrayList<State>>();
        partitionGroup.add(new ArrayList<State>());

        // Adds finals to group 0, and nonfinals to group 1
        for (State state : storageStates) {
            if (state.isBFinal())
            {
                partitionGroup.get(0).add(state);
            }
        }

        for (State state : storageStates) {
            if (!state.isBFinal())
            {
                partitionGroup.get(1).add(state);
            }
        }

        boolean expandable = true;
        // Checks if equivalent
        if (checkGroupInitials(partitionGroup, initState1, initState2)) 
        {
            while (expandable) {
                ArrayList<ArrayList<State>> newGroup = new ArrayList<ArrayList<State>>();
                newGroup = expandOnce(partitionGroup, m1.getInputs());

                if (newGroup != null) {
                    partitionGroup = newGroup;
                }
                else {
                    if (checkGroupInitials(newGroup, initState1, initState1)) 
                    {
                        return true;
                    }
                    else return false;
                }

            }
        }
        else {
            return false;
        }
    }

    public boolean checkGroupInitials(ArrayList<ArrayList<State>> pGroup, State s1, State s2) {
        int counter = 0;
        for (ArrayList<State> group : pGroup)
        {
            for (State state : group) 
            {
                if (state.equals(s1)) counter++;
                else if (state.equals(s2)) counter++;
            }

            if (counter == 1) return false;
            else if (counter == 2) return true;
        }
        return false;
    }


    public Machine convertToDFA(Machine m) {

        // Reference to states
        ArrayList<State> mStates = m.getStates();

        ArrayList<State> newStates = new ArrayList<State>();
        State initState = m.getInitialState();

        newStates.add(new State(initState));
        
        // CREATES A STORE STATE
        ArrayList<State> storageStates = new ArrayList<State>();
        for (State state : mStates) {
            storageStates.add(new State(state));
        }

        // Deep Clones Transitions
        for (int i = 0; i < mStates.size(); i++) 
        {
            for (Transition trSt : mStates.get(i).getTransitions()) 
            {
                for (State storStat : storageStates) 
                {
                    if (trSt.getDest().equals(storStat)) 
                    {
                        storageStates.get(i).makeTransition(storStat, trSt.getInput());
                        break;
                    }
                }        
            }
        }

        for (State state : storageStates) {
            if (state.isBInitial()) 
            {
                initState = state;
                break;
            }
        }

        // Since we're just reusing the same list, just feed the index in order to duplicate it!
        ArrayList<Integer> newTableIndex = new ArrayList<Integer>();
        ArrayList<State> expanded = new ArrayList<State>();

        // NFA Expander and connected
        State cur_state = initState;


        /* --------------- Dead State Code --------------- */
        State deadState = new State("_deadState", false, false);
        boolean isDeadHere = false;

        for (String input : m.getInputs()) 
        {
            deadState.makeTransition(deadState, input);
        }
        /* --------------- END OF DEADSTATE --------------- */

        // Initializes loop
        ArrayList<State> stateStack = new ArrayList<State>();
        stateStack.add(cur_state);
        expanded.add(cur_state);

        // Stops once machine finds there is no more to add
        while (cur_state != null) {
            // Checks each input of said machine
            for (String input : m.getInputs()) 
            {
                // Checks first if this state already exists
                String newName = cur_state.getTransitionString(input);
                State possibleState = doesStateExist(newName, storageStates);
                State possibleCurList = doesStateExist(newName, expanded);

                // Upon empty transitions at said input, connect cur_state to dead state
                if (newName == null) {

                    // Adds dead state to list since it exists
                    if (!isDeadHere)
                    {
                        isDeadHere = true;
                        expanded.add(deadState);   
                    }

                    // Sets empty transition to transition to this dead state instead
                    cur_state.makeTransition(deadState, input);

                    break;
                }

                if (possibleState != null) 
                {
                    // Since we have a reference to this in the original table already
                    // there is NO need to do anything.

                    // This adds the index of which state to get from.
                    for (int i = 0; i < storageStates.size(); i++) {
                        if (possibleState.equals(storageStates.get(i))) {
                            newTableIndex.add(i);
                            break;
                        }
                    }

                    // If this is an NFA connection (A->B) && (A->C),
                    // replace transition to existing combined state.
                    if (cur_state.getTransitions().size() > 1) {
                        // (Connects it to the reference in the new list.
                        // Since it's dynamic, it makes no  difference if
                        // it's in the new or old list)
                        cur_state.replaceTransitions(input, possibleCurList);
                    }

                    // Check if it's in the new expanded list before adding.
                    if (possibleCurList == null) expanded.add(possibleState);

                    break;
                }
                else
                {
                    // We found a DFA input. We will add every transition this way.
                    // Set final later.
                    State createState = new State(newName, false, false);

                    // Add transitions to new state at X input.
                    // This will add transitions for all inputs.

                    //e.g. First grab A's transitions (A-> C) && (A -> D)
                    for (Transition trans : cur_state.getTransitions(input)) 
                    {
                        // Since one of the states are final, the new state is final.
                        if (trans.getDest().isBFinal()) createState.setBFinal(true);

                        // in This inner loop, we check for their transitions (C -> B) && (D -> E)
                        for (Transition innerTrans : trans.getDest().getTransitions()) 
                        {
                            createState.makeTransition(innerTrans.getDest(), innerTrans.getInput());
                        }
                    }


                    // Since the state is now complete, we add its index
                    // and we add it to the main list as an official state.
                    newTableIndex.add(storageStates.size());
                    storageStates.add(createState);
                    stateStack.add(createState);

                    expanded.add(createState);

                    // make original state connect to new state instead.
                    cur_state.replaceTransitions(input, createState);
                }
                
            }
            // changes cur_state to whatever is in queue
            stateStack.remove(0);
            if (stateStack.size() > 0) {cur_state = stateStack.get(0);}
            else cur_state = null;
        }



        // Create machine Here
        Machine newMachine = new Machine(m.getName(), m.getTransitionNum());
        newMachine.setInputs(m.getInputs());
        newMachine.cloneStates(expanded);

        // REDUCE HERE
        Machine reduced = partitionAlgorithm(newMachine);

        return reduced;
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

    /**
     * Checks if there is at least one state that is the same BY NAME
     * @param s state to check
     * @param group to check if it exists in
     * @return said state, else null
     */
    public State doesStateExist(State s, ArrayList<State> group) {
        for (State state : group) 
        {
            if (s.equals(state)) return state;
        }
        return null;
    }

    public State doesStateExist(String s, ArrayList<State> group) {
        for (State state : group) 
        {
            if (s.equals(state.getName())) return state;
        }
        return null;
    }
}
