import java.util.ArrayList;

public class State {
    String name;
    Boolean bInitial;
    Boolean bFinal; // Changed into bFinal since final is a reserved token
    ArrayList<Transition> transitions;

    /**
     * Constructs a state given a name and initial/final state transition.
     * @param name is the name of state
     * @param bInitial determines if initial
     * @param bFinal determines if acceptor
     */
    public State(String name, Boolean bInitial, Boolean bFinal) {
        this.name = name;
        this.bInitial = bInitial;
        this.bFinal = bFinal;
        this.transitions= new ArrayList<Transition>();
    }

    /**
     * Checks if a state is the same state it's looking for based on name. This is
     * due to the presence of deep-cloned machines. We based them off of name now.
     * @param s
     * @return
     */
    public boolean equals(State s) {
        return this.name.equals(s.getName());
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
            out.concat  (
                            "Transition# " + ctr +"\n" +
                            transition.toString()
                            +"\n"
                        );
            ctr++;
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
        for (Transition t : transitions) {
            if (t.getInput().equals(input) && t.getDest().equals(dest)) {
                System.out.println("\n\n [MOST LIKELY NFA PRINT] found a transition to the same destination. Ignoring . . . \n\n");
                safe = false;
                break;
            }
        }

        // Makes it so that it only adds the transition IF it is not a duplicate.
        if (safe) transitions.add(new Transition(this, dest, input));
    }

    /**
     * This is for when transitions added to this state are obviously someone else's.
     */
    public void normalizeTransitions() {
        for (Transition trans : transitions) {
            trans.setSource(this);
        }
    }

    /**
     * This replaces current transitions with one new transition.
     * Made for NFA transition replacement.
     * @param input string input to watch
     * @param dest string to transition to
     */
    public void replaceTransitions(String input, State dest) {
        for (Transition transition : transitions) {
            if (transition.getInput().equals(input)) 
            {
                transitions.remove(transition);
            }
        }

        this.makeTransition(dest, input);
    }

    public String getName() {
        return this.name;
    }

    public Boolean isBInitial() {
        return this.bInitial;
    }

    public Boolean isBFinal() {
        return this.bFinal;
    }

    public ArrayList<Transition> getTransitions() {
        return this.transitions;
    }

    public ArrayList<Transition> getTransitions(String input) {
        ArrayList<Transition> transList = new ArrayList<Transition>();
        for (Transition transition : transitions) {
            if (transition.getInput().equals(input)) transList.add(transition)
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
            finalName.concat(transition.getDest().getName());
        }
        return finalName;
    }
    
    // public void setName(String name) {
    //     this.name = name;
    // }

    // public void setBInitial(Boolean bInitial) {
    //     this.bInitial = bInitial;
    // }
    
    public void setBFinal(Boolean bFinal) {
        this.bFinal = bFinal;
    }

    // public void setTransitions(ArrayList<Transition> transitions) {
    //     this.transitions = transitions;
    // }


}
