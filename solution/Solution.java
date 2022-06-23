import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;

public class Solution {
    public static void main(String[] args) throws IOException {
        //True if phase 1, false if phase 2
        boolean bPhase = false;
        ArrayList<Machine> machine = new ArrayList<Machine>();
        boolean bEquivalent;
        String input, mName, mInitial, mTrans, mState, mFinal;
        int numMachines, nStates, nInputs, nTrans, nFinals;
        int i, j, k, l, m, n, o, p;
        Fixer fix = new Fixer();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));


        if (bPhase){
          numMachines = 2;
        }
        else{
          input = buffer.readLine();
          numMachines = Integer.parseInt(input);
          String nothing = buffer.readLine();
        }

        for (i = 0; i < numMachines; i++){
          ArrayList<State> allStates = new ArrayList<State>();
          ArrayList<String> allInputs = new ArrayList<String>();

          mName = buffer.readLine();
          nStates = Integer.parseInt(buffer.readLine());

          //uses number of states to create the states of the machine
          for (j = 0; j < nStates; j++){
            mState = buffer.readLine();
            allStates.add(new State(mState));
          }

          nInputs = Integer.parseInt(buffer.readLine());

          //uses number of inputs to create the inputs of the machine
          for (k = 0; k < nInputs; k++){
            //add validation if needed
            allInputs.add(buffer.readLine());
          }

          nTrans = Integer.parseInt(buffer.readLine());
          machine.add(new Machine (mName, nTrans));

          //uses number of states to create the states of the machine
          for (l = 0; l < nTrans; l++){
            mTrans = buffer.readLine(); //gets the transition line
            String tName, tIn, tDest;
            String[] transitions = new String[3];
            transitions = mTrans.split("\\s+");
            tName = transitions[0];
            tIn = transitions[1];
            tDest = transitions[2];

            //find the state and make the transitions
            for (m = 0; m < allStates.size(); m++){
              if (allStates.get(m).equals(tName)){

                for (n = 0; n < allStates.size(); n++){
                  if (allStates.get(n).equals(tDest)){
                    allStates.get(m).makeTransition(allStates.get(n), tIn);
                    // allStates.get(m).displayTransitions();
                  }
                }
              }
            }

        }

        // find state and set as initial
        mInitial = buffer.readLine();
        for (m = 0; m < allStates.size(); m++){
          if (allStates.get(m).equals(mInitial)){
            allStates.get(m).setBInitial(true);
          }
        }

        nFinals = Integer.parseInt(buffer.readLine());
        for (n = 0; n < nFinals; n++){
          mFinal = buffer.readLine();
          // find state and set as final
          for (m = 0; m < allStates.size(); m++){
            if (allStates.get(m).equals(mFinal)){
              allStates.get(m).setBFinal(true);
            }
          }
        }
        machine.get(i).setStates(allStates);
        machine.get(i).setInputs(allInputs);

        //check if NFA
        if (!machine.get(i).isMDFA()){
          // Machine mTemp = machine.get(i);
          machine.set(i, fix.convertToDFA(machine.get(i)));
        }

        String nothing;
        if (i < numMachines)
          nothing = buffer.readLine();
      }

      // ArrayList<Machine> mEquivalent = new ArrayList<Machine>;
      // ArrayList<Machine> mNotEquivalent = new ArrayList<Machine>;
      ArrayList<ArrayList<String>> mEquivalent = new ArrayList<ArrayList<String>>();
      boolean checked = false;
      // ArrayList<String> mEquivalent = new ArrayList<String>;
      // String mEquivalent = "";

      for (int r = 0; r < machine.size(); r++){
        ArrayList<String> cluster = new ArrayList<String>();
        checked = false;
        for (int s = r; s < machine.size(); s++){

          //check if that machine has already been added to an existing cluster
          for (int t = 0; t < mEquivalent.size(); t++){
            for (int u = 0; u < mEquivalent.get(t).size(); u++){
              if (mEquivalent.get(t).get(u).equals(machine.get(r).getName()))
                checked = true;
              // System.out.println("\n" + mEquivalent.get(t).get(u) + " and " + machine.get(r).getName() + " : " + checked);
            }

          }

          //check if you're checking equivalence of the same machine
          if (!machine.get(r).equals(machine.get(s)) && !checked){
            // System.out.println("\nYou are comparing " + machine.get(r).getName() + " and " + machine.get(s).getName());
            if (!cluster.contains(machine.get(r).getName()))
              cluster.add(machine.get(r).getName());
            // compare r and s machines
            if (fix.isEquivalent(machine.get(r), machine.get(s))){
              // System.out.println("**Equivalent");
              cluster.add(machine.get(s).getName());
            }
            else {
              // System.out.println("**Not Equivalent");
            }
          }
        }

        if (cluster.size() > 0){
          System.out.print("\nCluster: ");
          for (int temp = 0; temp < cluster.size(); temp++){
            if (!cluster.get(temp).isEmpty())
              System.out.print(cluster.get(temp) + " ");
          }

          mEquivalent.add(cluster);
        }

        System.out.println();

      }
      if(bPhase) View.phase1Print(fix.isEquivalent(machine.get(0), machine.get(1)));
      // else View.phase2Print(arrEquivalent);

      // after using View class, use .flush() to close buffered writer
      View.out.flush();
    }
}

class View {
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
            out.write("\nMachine Name: " + m.getName() + "\nInputs:\n");
            for (i = 0; i < m.getInputs().size(); i++) {
                out.write(m.getInputs().get(i) + "\n");
            }
            out.write("States:\n");
            for (i = 0; i < m.getStates().size(); i++) {
                out.write(m.getStates().get(i).name);
                tempState = m.getStates().get(i);
                out.write("\nTransitions Size: " + tempState.getTransitions().size() + "\nTransitions:\n");
                out.write(m.getStates().get(i).displayTransitionsSimple());
                // for (j = 0; j < tempState.getTransitions().size(); j++) {
                //     out.write(tempState.getTransitions().get(j).toString());
                //     //out.write(tempState.displayTransitions());
                // }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class Machine {
    String name;
    int TransitionNum;
    ArrayList<State> states;
    ArrayList<String> inputs;

    public Machine(String name, int TransitionNum) {
        this.name = name;
        this.TransitionNum = TransitionNum;
    }

    /**
     * Creates a clone machine while deep cloning every state and input
     * @param m is machine to clone
     * @return a new deep cloned machine
     */
    public Machine cloneMachine(Machine m) {
        Machine clone = new Machine(m.getName(), m.getTransitionNum());
        for (State state: m.getStates()) {
            clone.states.add(new State(state));
        }

        // This deep clones every transition properly after all states are initialized.
        for (int i = 0; i < m.getStates().size(); i++)
        {
            for (Transition t : m.getStates().get(i).getTransitions())
            {
                State dest = null;
                for (int j = 0; j < m.getStates().size(); j++)
                {
                    State destRef = clone.getStates().get(j);
                    if (t.getDest().equals(destRef))
                    {
                        dest = destRef;
                        break;
                    }
                }
                clone.getStates().get(i).makeTransition(dest, t.getInput());
            }

        }


        return clone;
    }

    public void cloneStates(ArrayList<State> cloneList) {
        ArrayList<State> newStates = new ArrayList<State>();

        for (State state: cloneList) {
            newStates.add(new State(state));
        }

        // This deep clones every transition properly.
        for (int i = 0; i < cloneList.size(); i++) {
            for (Transition t : cloneList.get(i).getTransitions())
            {
                State dest = null;
                for (int j = 0; j < cloneList.size(); j++)
                {
                    // We get this destination state from our new list.
                    // Since the states of cloneList and newStates are the same, we're simply
                    // duplicating the transition in our own newState.
                    State destRef = newStates.get(j);
                    if (t.getDest().equals(destRef))
                    {
                        dest=destRef;
                        break;
                    }
                }

                // Each i iteration looks for a state to check their transitions
                // Each j iteration looks at the list of states said state could be looking at
                // When we find that, here we get i-State to make a transition to that j-State we found.
                newStates.get(i).makeTransition(dest, t.getInput());
            }
        }

        // This formally makes the machine use the new states.
        this.states.addAll(newStates);
    }

    public Boolean isMDFA (){
        for (State state : states)
        {
            for (String t : inputs)
            {
                if (state.getTransitions(t) != null && state.getTransitions(t).size() == 1) {}
                else return false;
            }
        }
        return true;
    }

    /**
     * Simply grabs the initial state of the machine by looping through all states.
     * @return initial state of machine. null if none
     */
    public State getInitialState() {
        for (State state : states) {
            if (state.isBInitial()) return state;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public int getTransitionNum() {
        return this.TransitionNum;
    }

    public ArrayList<State> getStates() {
        return this.states;
    }

    public ArrayList<String> getInputs() {
        return this.inputs;
    }
    public void setStates(ArrayList<State> states) {
        this.states = states;
    }

    public void setInputs(ArrayList<String> inputs) {
        this.inputs = inputs;
    }
}

class State {
    String name;
    boolean bInitial;
    boolean bFinal; // Changed into bFinal since final is a reserved token
    ArrayList<Transition> transitions;

    /**
     * Constructs a state given a name and initial/final state transition.
     * @param name is the name of state
     * @param bInitial determines if initial
     * @param bFinal determines if acceptor
     */
    public State(String name, boolean bInitial, boolean bFinal) {
        this.name = name;
        this.bInitial = bInitial;
        this.bFinal = bFinal;
        this.transitions= new ArrayList<Transition>();
    }

    public State(String name) {
        this.name = name;
        this.transitions= new ArrayList<Transition>();
    }

    /**
     * Checks if a state is the same state it's looking for based on name. This is
     * due to the presence of deep-cloned machines. We based them off of name now.
     * @param s
     * @return
     */
    public boolean equals(String s) {
        return this.name.equals(s);
    }

    public boolean strictlyEqual(State s) {
        return this.equals(s);
    }

    /**
     * Overrides toString function to display full details of state
     * @return statename, if initial state, and if acceptor
     */
    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", bInitial='" + isBInitial() + "'" +
            ", bFinal='" + isBFinal() + "'" +
            "}";
    }

    /**
     * Serves as clone function for DEEP cloning instead of shallow cloning.
     * @param s is state to clone
     * @return new state instance
     */
    public State(State s) {
        this.name = s.name;
        this.bInitial = s.isBInitial();
        this.bFinal = s.isBFinal();
    }

    /**
     * For Display: String info of all transitions from current state to other destination states
     *
     * Note: I separated this from toString to avoid information overload
     * while having the option to display the  transitions when needed.
     *
     * @return String info of all transitions from current state to other destination states
     */
    public String displayTransitions (){
        String out ="";
        int ctr = 0;
        for (Transition transition : transitions) {
            out= out.concat  (
                            "\nTransition# " + ctr +"\n" +
                            transition.toString()
                            +"\n"
                        );
            ctr++;
        }
        return out;
    }

    public String displayTransitionsSimple() {
        String out ="";
        for (Transition transition : transitions) {
            out = out.concat(transition.simpleString()+"\n");
        }
        return out;
    }

    /**
     * Adds new transition to the Transitions Arraylist
     *
     * @param dest destination state
     * @param input input needed to go to destination from source
     */
    public void makeTransition (State dest, String input){
        boolean safe = true;
        if (transitions != null)
        {
            for (Transition t : transitions)
            {
                if (t.getInput().equals(input) && t.getDest().equals(dest)) {
                    // System.out.println("\n\n [MOST LIKELY NFA PRINT] found a transition to the same destination. Ignoring . . . \n\n");
                    safe = false;
                    break;
                }
            }
        }
        else {transitions = new ArrayList<Transition>();}

        // System.out.println("Is it safe?: "+safe);
        // Makes it so that it only adds the transition IF it is not a duplicate.
        if (safe) transitions.add(new Transition(this, dest, input));
        // System.out.println("Display!!!: "+this.displayTransitions());
        // if (safe) System.out.println("Display?: "+this.transitions.get(0).toString());
    }

    /**
     * This is for when transitions added to this state are obviously someone else's.
     */
    public void normalizeTransitions(ArrayList<String> inputs) {
        for (String in : inputs)
        {
            // Compare destinations under a certain input
            for (Transition t : getTransitions(in))
            {
                for (int j = 0; j < getTransitions(in).size(); j++)
                {
                    Transition t2 = getTransitions(in).get(j);

                    if (t.equals(t2)) continue;

                    if (t.getDest().getName().contains(t2.getDest().getName()))
                    {
                        // System.out.println("Removing: "+t2.getDest().getName());
                        // System.out.println("Because: "+t.getDest().getName());
                        transitions.remove(t2);
                        j--;
                    }
                }
            }
        }
    }

    /**
     * This replaces current transitions with one new transition.
     * Made for NFA transition replacement.
     * @param input string input to watch
     * @param dest string to transition to
     */
    public void replaceTransitions(String input, State dest) {

        // System.out.println("Replacing transitions.");
        // System.out.println(this.displayTransitionsSimple());

        for (int i = 0; i < transitions.size(); i++) {
            if (transitions.get(i).getInput().equals(input))
            {
                transitions.remove(transitions.get(i));
                i--;
            }
        }

        this.makeTransition(dest, input);
    }

    public String getName() {
        return this.name;
    }

    public boolean isBInitial() {
        return this.bInitial;
    }

    public boolean isBFinal() {
        return this.bFinal;
    }

    public ArrayList<Transition> getTransitions() {
        if (this.transitions == null) return null;
        if (this.transitions.size()  <= 0) return null;
        return this.transitions;
    }

    public ArrayList<Transition> getTransitions(String input) {
        ArrayList<Transition> transList = new ArrayList<Transition>();

        if (transitions != null)
        {
            for (Transition transition : transitions) {
                if (transition.getInput().equals(input)) transList.add(transition);
            }
        }
        return transList;
    }

    /**
     * Gets a full transition string (for DFAs) e.g. A-> B and A->C will return A -> BC
     * @param input string input to check
     * @return stringname
     */
    public String getTransitionString(String input) {
        ArrayList<Transition> transList = getTransitions(input);
        String finalName = "";

        if (transList.size() <= 0) return null;

        // Concats every string name at destination
        for (Transition transition : transList)
        {
            finalName = finalName.concat(transition.getDest().getName());
        }
        return finalName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBInitial(boolean bInitial) {
        this.bInitial = bInitial;
    }

    public void setBFinal(boolean bFinal) {
        this.bFinal = bFinal;
    }
}

class Transition {
    private State source;
    private State dest;
    private String input;

    public Transition(State source, State dest, String input) {
        this.source = source;
        this.dest = dest;
        this.input = input;
    }

    public State getSource() {
        return this.source;
    }

    public State getDest() {
        return this.dest;
    }

    public String getInput() {
        return this.input;
    }

    public void setSource(State source) {
        this.source = source;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString()
    {
        // I did not include the transitions attribute
        // As I thought it might made it confusing
        //(I reserved it to be include in State toString)
        String strSource =  " source: --"+
                            "\n  name: " + source.getName() +
                            "\n  bInitial: "+ source.isBInitial()+
                            "\n  bFinal"+ source.isBFinal()+
                            "\n";

        String strDest = " dest: --"+
                        "\n  name: " + dest.getName() +
                        "\n  bInitial: "+ dest.isBInitial()+
                        "\n  bFinal"+ dest.isBFinal()+
                        "\n";

        return  "{"+ strSource +
                "input: " + this.input + "\n" +
                strDest +
                "}\n";
    }

    public String simpleString() {
        return (this.source.getName()+" "+this.input+" "+this.dest.getName());
    }
}

class Fixer {

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
            // System.out.println("Current Stack: ");
            // System.out.println(stateStack+"\n\n");

            // System.out.println("We're converting.. Now at: "+cur_state.getName());
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
                    // System.out.println("Look dead state found! at "+cur_state.getName());

                    // Adds dead state to list since it exists
                    if (!isDeadHere)
                    {
                        isDeadHere = true;
                        expanded.add(deadState);
                    }

                    // Sets empty transition to transition to this dead state instead
                    cur_state.makeTransition(deadState, input);

                    // break;
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
