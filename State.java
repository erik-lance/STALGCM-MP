import java.util.ArrayList;

public class State {
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

        // TODO: Check if || null is bad
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
