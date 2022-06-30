import java.util.ArrayList;
import java.util.HashSet;

public class Fixer {

    public boolean isEquivalent(Machine m1, Machine m2) {
        // Reference to states
        ArrayList<State> mStates1 = m1.getStates();
        ArrayList<State> mStates2 = m2.getStates();

        // CREATES A STORE STATE
        ArrayList<State> storageStates = new ArrayList<State>();

        //Machine cloning
        storageStates.addAll(deepCloneStates(mStates1));
        storageStates.addAll(deepCloneStates(mStates2));

        // Rename to the necessary states.
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                            "1","2","3","4","5","6","7","8","9",
                            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                            ""
                        
                            };
        int alphCounter = 0;

        // We want to rename the states properly to avoid mixups. This won't affect transitions since transitions
        // hold a "State" type. Their names should update automatically.
        for (State state : storageStates)
        {
            state.setName(alphabet[alphCounter]);
            alphCounter++;
        }

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


        // Checks if equivalent
        if (checkGroupInitials(partitionGroup))
        {
            while (expandable) {
                ArrayList<ArrayList<State>> newGroup = new ArrayList<ArrayList<State>>();
                newGroup = expandOnce(partitionGroup, m1.getInputs());



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

        State initState = m.getInitialState();

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
        State deadState = new State("_dd", false, false);
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
                if (newName == null)
                {
                    // Adds dead state to list since it exists
                    if (!isDeadHere)
                    {
                        isDeadHere = true;
                        expanded.add(deadState);
                    }

                    // Sets empty transition to transition to this dead state instead
                    cur_state.makeTransition(deadState, input);
                }
                else
                {
                    // Checks if the cur_state is transitioning to a state that already exists
                    if (possibleState != null)
                    {
                        // System.out.println("Found state in orig list! for "+possibleState.getName());
                        // Since we have a reference to this in the original table already
                        // there is NO need to do anything.

                        // Check if it's in the new expanded list before adding.
                        if (possibleCurList == null)
                        {
                            // System.out.println("Adding directly to list! "+possibleState.getName());
                            expanded.add(possibleState);
                            stateStack.add(possibleState);
                        }

                        // If this is an NFA connection (A->B) && (A->C),
                        // replace transition to existing combined state.
                        if (cur_state.getTransitions(input).size() > 1)
                        {
                            // System.out.print("Normalizing a transition! Of "+cur_state.getName()+" to a certain "+newName);
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
                        else
                        {

                        }
                    }
                    else
                    {
                        // We found a NFA input. We will add every transition this way.
                        // Set final later.

                        // System.out.println("NFA transition at "+cur_state.getName());

                        State createState = new State(newName, false, false);

                        // Add transitions to new state at X input.
                        // This will add transitions for all inputs.

                        //e.g. First grab A's transitions (A-> C) && (A -> D)
                        // and THEN grabe the other's

                            // System.out.println("Let's make for: "+input);
                            // For each transition under a certain input
                            for (Transition trans : cur_state.getTransitions(input))
                            {
                                // Since one of the states are final, the new state is final.
                                if (trans.getDest().isBFinal()) createState.setBFinal(true);

                                // in This inner loop, we check for their transitions (C -> B) && (D -> E)
                                // System.out.println("We're looking at the transitions of "+trans.getDest().getName());

                                // Get connection to null state
                                // if (trans.getDest().getTransitions() == null) 
                                // {
                                //     if (!isDeadHere)
                                //     {
                                //         isDeadHere = true;
                                //         expanded.add(deadState);
                                //     }

                                //     // Sets empty transition to transition to this dead state instead
                                //     for (String in : m.getInputs()) 
                                //     {
                                //         createState.makeTransition(deadState, in);
                                //     }
                                // }
                                // else
                                // {
                                //     if (!isDeadHere)
                                //     {
                                //         isDeadHere = true;
                                //         expanded.add(deadState);

                                //         ArrayList<String>  missingInputs = new ArrayList<String>();
                                //         HashSet<String> existInputs = new HashSet<String>();
                                //         for (Transition t : trans.getDest().getTransitions()) 
                                //         {
                                //             existInputs.add(t.getInput());
                                //         }


                                //         for (String in : m.getInputs()) 
                                //         {
                                //             boolean existing = false;
                                //             for (String not : existInputs) 
                                //             {
                                //                 if (not.equals(in))
                                //                 {
                                //                     existing = true;
                                //                     break;
                                //                 }
                                //             }
                                //             if (!existing) missingInputs.add(in);
                                //         }

                                //         for (String addS: missingInputs ) 
                                //         {
                                //             createState.makeTransition(deadState, addS);   
                                //         }
                                //     }
                                    
                                // }
                                
                                if (trans.getDest().getTransitions() == null) continue;

                                for (Transition innerTrans : trans.getDest().getTransitions())
                                {
                                    // We simply made a transition to the same destination as the states it's copying.
                                    /* e.g.: We have A -> BC
                                    * BC's transitions = B.trans + C.trans
                                    * new state "BC".transitions = makeTransition(B.trans); and for C as well.
                                    */
                                    // System.out.println("Let's go here!: "+createState.getName()+" at "+innerTrans.getInput()+" to: "+innerTrans.getDest().getName());
                                    createState.makeTransition(innerTrans.getDest(), innerTrans.getInput());
                                }
                            }


                        // System.out.println("New NFA state acquired! "+createState.getName());
                        // System.out.println("State Details:\n"+createState.displayTransitionsSimple());

                        // To remove duplicates.
                        createState.normalizeTransitions(m.getInputs());

                        // Since the state is now complete, we add its index
                        // and we add it to the main list as an official state.
                        storageStates.add(createState);
                        stateStack.add(createState);

                        expanded.add(createState);

                        // make original state connect to new state instead.
                        cur_state.replaceTransitions(input, createState);
                    }
                }



            }
            // changes cur_state to whatever is in queue
            stateStack.remove(0);
            if (stateStack.size() > 0) {cur_state = stateStack.get(0);}
            else cur_state = null;
        }

        // System.out.println("\n\nHI FIXER HERE, WE FIXED NFA TO DFA ! HERE'S MY RESULT:");
        // for (State state : expanded) {
        //     System.out.println("\n"+state.getName());
        //     System.out.println(state.toString());
        // }
        //
        // printStateDets(expanded);

        // Create machine Here
        Machine newMachine = new Machine(m.getName(), m.getTransitionNum());
        newMachine.setInputs(m.getInputs());
        newMachine.setStates(expanded);


        // // REDUCE HERE
        // Machine reduced = partitionAlgorithm(newMachine);

        return newMachine;
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
        partitions.add(new ArrayList<State>());



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

        // In case there are no rejectors.
        if (partitions.get(0).size() != m.getStates().size()) partitions.add(new ArrayList<State>());

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
        // System.out.println("We are now at state reduction!");

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

        // for (ArrayList<State> gg : expanded)
        // {
        //     System.out.println("TEST3: ");
        //     for (State sss : gg)
        //     {
        //         System.out.println(sss.toString());
        //     }
        // }

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
        if (s == null) return null;

        // System.out.println("Let's find "+s);
        for (State state : group)
        {
            char[] content = state.getName().toCharArray();
            java.util.Arrays.sort(content);
            String sorted = new String(content);

            content = s.toCharArray();
            java.util.Arrays.sort(content);
            String sorted2 = new String(content);

            if (sorted.equals(sorted2)) return state;
            // if (state.getName().contains(s)) return state;
        }

        // System.out.println(s+" does not exist yet\n\n");

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
            if (group.get(i).getTransitions() ==  null) continue;
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
