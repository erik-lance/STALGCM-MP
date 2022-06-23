import java.util.ArrayList;
import java.util.HashSet;

public class Fixer {

    public boolean isEquivalent(Machine m1, Machine m2) {
        // Reference to states
        ArrayList<State> mStates1 = m1.getStates();
        ArrayList<State> mStates2 = m2.getStates();

        State initState1 = m1.getInitialState();
        State initState2 = m2.getInitialState();

        // CREATES A STORE STATE
        ArrayList<State> storageStates = new ArrayList<State>();

        //Machine cloning
        storageStates.addAll(deepCloneStates(mStates1));
        storageStates.addAll(deepCloneStates(mStates2));

        // Rename to the necessary states.
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int alphCounter = 0;

        // We want to rename the states properly to avoid mixups. This won't affect transitions since transitions
        // hold a "State" type. Their names should update automatically.
        for (State state : storageStates) 
        {
            state.setName(alphabet[alphCounter]);    
            alphCounter++;
        }

        for (State state : storageStates) {
            if (state.isBInitial()) initState1 = state;
        }

        for (State state : storageStates) {
            if (state.isBInitial() && !state.equals(initState1)) initState2 = state;
        }

        //TODO: remove these
        // System.out.println("Initial:: ");
        // printStateList(storageStates);

        // System.out.println("Trans: ");
        // printStateDets(storageStates);

        // ------ STATES COMPLETE; PARTITION STARTS HERE ------  //
        ArrayList<ArrayList<State>> partitionGroup = new ArrayList<ArrayList<State>>();
        partitionGroup.add(new ArrayList<State>());
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

        int ctr = 0;

        // Checks if equivalent
        if (checkGroupInitials(partitionGroup)) 
        {
            while (expandable) {
                // for (ArrayList<State> ppGroup : partitionGroup) 
                // {
                //     System.out.println("TEST: "+ctr);
                //     for (State pppGroup : ppGroup) 
                //     {
                //         System.out.println(pppGroup.toString());
                //     }    
                // }


                ArrayList<ArrayList<State>> newGroup = new ArrayList<ArrayList<State>>();
                newGroup = expandOnce(partitionGroup, m1.getInputs());

                // For printing
                // if (newGroup != null) 
                // {
                //     for (ArrayList<State> gg : newGroup) 
                //     {
                //         System.out.println("TEST2: "+ctr);
                //         for (State sss : gg) 
                //         {
                //             System.out.println(sss.toString());
                //         }    
                //     }
                // }
                

                if (newGroup != null) {
                    partitionGroup = newGroup;
                }
                else {
                    if (checkGroupInitials(partitionGroup)) 
                    {
                        return true;
                    }
                    else return false;
                }

            }
        }
        return false;
    }

    /**
     * Simply checks if initial states are in the same partition.
     * @param pGroup partition to check
     * @return return true if in same partition, else false
     */
    public boolean checkGroupInitials(ArrayList<ArrayList<State>> pGroup) {
        for (ArrayList<State> group : pGroup)
        {
            int counter = 0;
            for (State state : group) 
            {
                if(state.isBInitial()) counter++;
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
        storageStates.addAll(deepCloneStates(mStates));

        for (State state : storageStates) {
            if (state.isBInitial()) 
            {
                initState = state;
                break;
            }
        }

        // Since we're just reusing the same list, just feed the index in order to duplicate it!
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

                // Checks if the cur_state is transitioning to a state that already exists
                if (possibleState != null) 
                {
                    // Since we have a reference to this in the original table already
                    // there is NO need to do anything.

                    // Check if it's in the new expanded list before adding.
                    if (possibleCurList == null) 
                    {
                        expanded.add(possibleState);
                        stateStack.add(possibleState);
                    }

                    // If this is an NFA connection (A->B) && (A->C),
                    // replace transition to existing combined state.
                    if (cur_state.getTransitions(input).size() > 1) {
                        // (Connects it to the reference in the new list.
                        // Since it's dynamic, it makes no  difference if
                        // it's in the new or old list)
                        cur_state.replaceTransitions(input, possibleCurList);

                        /* What's happening:
                         * cur_state's transition to input X is already an existing state.
                         * however, this is actually an NFA connection (because we were looking at A -> ['B','C'])
                         * so it replaces those separate transitions into a complete transition to an existing "BC" state.
                         * it should exist because possibleState isn't null.
                         */
                    }
                    break;
                }
                else
                {
                    // We found a NFA input. We will add every transition this way.
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
                            // We simply made a transition to the same destination as the states it's copying.
                            /* e.g.: We have A -> BC
                             * BC's transitions = B.trans + C.trans
                             * new state "BC".transitions = makeTransition(B.trans); and for C as well.
                             */
                            createState.makeTransition(innerTrans.getDest(), innerTrans.getInput());
                        }
                    }


                    // Since the state is now complete, we add its index
                    // and we add it to the main list as an official state.
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

        System.out.println("\n\nHI FIXER HERE, WE FIXED NFA TO DFA ! HERE'S MY RESULT:");
        for (State state : expanded) {
            System.out.println("\n"+state.getName());
            System.out.println(state.toString());
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

        // This is an array list of an array list of states. This will arrange them
        // based on their "group" number.

        // E.g. :Group 0, State 0
    ArrayList<ArrayList<State>> partitions = new ArrayList<ArrayList<State>>();


        // Initial Partitioning

        // Get all End States
        for (State mState : m.getStates()) {
            int i = 0;
            if (mState.isBFinal())
            {
                partitions.get(0).add(mState);
                i++;
            }

            // No final states, therefore this machine is invalid.
            if (i == 0) return null;
        }

        // Get all non-end states. (Can't be subjected to else state above in case there is no end state yet)
        for (State mState : m.getStates()) {
            if (!mState.isBFinal())
            {
                partitions.get(1).add(mState);
            }
        }

        
        /* ---------- Full Algorithm Section ---------- */

        // Loop that calls expandOnce until fully expanded.
        boolean dividable = true;
        ArrayList<ArrayList<State>> newPartition = new ArrayList<ArrayList<State>>();
        newPartition.addAll(partitions);

        while (dividable) {
            
            ArrayList<ArrayList<State>> reduced = expandOnce(newPartition, m.inputs);

            newPartition = new ArrayList<ArrayList<State>>();

            if (reduced != null) newPartition.addAll(reduced);
            else dividable = false;

            // Haven't utilized the partition function to number each partition, for now ganito muna.
        }

        
        /* ----- State Reduction ----- */

        //TODO: DELETE
        System.out.println("We are now at state reduction!");

        ArrayList<State> finalPartition = reduceAndConnect(newPartition,m.getInputs());

        // the number of transitions is num of states * num of inputs since it is DFA.
        Machine finalMachine = new Machine(m.getName(), (finalPartition.size()*m.getInputs().size()));
        finalMachine.setStates(finalPartition);
        finalMachine.setInputs(m.getInputs());

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

        ArrayList<ArrayList<String>> stCode = new ArrayList<ArrayList<String>>();

        // For each group
        for (int i = 0; i < pGroup.size(); i++) 
        {
            stCode.add(new ArrayList<String>());
            // For each state in group
            for (int j = 0; j < pGroup.get(i).size(); j++) 
            {
                String transitionCode ="";

                // Checks each transition of found state to find their group code for each transition
                for (Transition t : pGroup.get(i).get(j).getTransitions()) 
                    transitionCode = transitionCode.concat(getDestGroup(pGroup, t.getDest()));

                stCode.get(i).add(transitionCode);
            }
        }

        ArrayList<ArrayList<State>> expanded = new  ArrayList<ArrayList<State>>();

        for (int i = 0; i < pGroup.size(); i++) 
        {
            // This makes it easier to collect only unique string transition values
            HashSet<String> hasher = new HashSet<String>();           
            for (String code : stCode.get(i)) {
                hasher.add(code);
            } 

            if (hasher.size() > 1) 
            {
                //Separation goes here.
                
                
                // Compare for each hashcode each state
                for (String hash : hasher) 
                {
                    ArrayList<State> grouped = new ArrayList<State>();
                    expanded.add(new ArrayList<State>());
                    for (int j = 0; j < pGroup.get(i).size(); j++)
                    {
                        if(stCode.get(i).get(j).equals(hash))
                        {
                            grouped.add(pGroup.get(i).get(j));
                        }
                    }

                    // Notice the index is different for expanded. This is because we expand a group
                    // from that index, therefore the index changes here.
                    expanded.get(expanded.size()-1).addAll(grouped);
                }
                
            }
            else 
            {
                // Fit states normally. Therefore just add them.
                expanded.add(new ArrayList<State>());
                expanded.get(expanded.size()-1).addAll(pGroup.get(i));
            }
        }

        for (ArrayList<State> gg : expanded) 
        {
            System.out.println("TEST3: ");
            for (State sss : gg) 
            {
                System.out.println(sss.toString());
            }    
        }

        // Returns null if list is found to be the same as it was. Indicating that this was the last partition.
        if (pGroup.equals(expanded)) return null;
        else return expanded;
    }

    /**
     * Returns the string group of where the said state will go to
     * @param pGroup is the partition group to check
     * @param dest is the destination state where the starting state will end up in
     * @return the string number of the group number.
     */
    public String getDestGroup(ArrayList<ArrayList<State>>pGroup, State dest) {
        
        // Loops through each state of each group
        int groupNum = 0;
        for (ArrayList<State> stateGroup: pGroup) {
            for (State state : stateGroup) {
                if (state.equals(dest)) 
                {
                    return String.valueOf(groupNum);
                }
                
            }
            groupNum++;
        }

        return null;
    }

    public ArrayList<State> reduceAndConnect(ArrayList<ArrayList<State>> s, ArrayList<String> inputs) {
        // Each group is one state.
        ArrayList<State> states = new ArrayList<State>();
        ArrayList<ArrayList<String>> stCode = new ArrayList<ArrayList<String>>();

        // For each group
        for (int i = 0; i < s.size(); i++) 
        {
            stCode.add(new ArrayList<String>());
            // Checks each transition of found state to find their group code for each transition
            for (Transition t : s.get(i).get(0).getTransitions()) 
                stCode.get(i).add(getDestGroup(s, t.getDest()));
        }


        for (ArrayList<State> list : s) 
        {
            State sampleState = list.get(0);
            states.add(new State(sampleState.getName(), sampleState.isBInitial(), sampleState.isBFinal()));
        }

        // make Transition each state
        int i = 0;
        for (State state : states) 
        {
            for (int j = 0; j < inputs.size(); j++) 
            {
                // First  index is the actual state num, second is the transition num
                int parse = Integer.parseInt(stCode.get(i).get(j));
                state.makeTransition(states.get(parse), inputs.get(j));
            }
            i++;
        }
        return states;
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

    public ArrayList<State> deepCloneStates(ArrayList<State> group) {
        ArrayList<State> clone = new ArrayList<State>();
        //States Cloning
        for (State state : group) {
            clone.add(new State(state));
        }

        // Deep Clones Transitions
        for (int i = 0; i < group.size(); i++) 
        {
            // Checks for each transition available in selected state from original group
            for (Transition trSt : group.get(i).getTransitions()) 
            {
                // Loops through each state in the clone array for a state with the same name as the destination.
                for (State storStat : clone) 
                {
                    // If we found a dest state from the original group that is in the clone group, copy the transition.
                    if (trSt.getDest().equals(storStat.getName())) 
                    {
                        clone.get(i).makeTransition(storStat, trSt.getInput());
                        break;
                    }
                }        
            }
        }

        return clone;

    }

    public void printStateList(ArrayList<State> s) {
        for (State state : s) {
            System.out.println("State: "+state.getName());
            System.out.println(state.toString()+"\n");
        }
    }

    public void printStateDets(ArrayList<State> s) {
        for (State state : s) {
            System.out.println("State: "+state.getName());
            System.out.println(state.displayTransitionsSimple());
        }
    }
}
