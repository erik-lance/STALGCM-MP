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
        transitions.add(new Transition(this, dest, input));
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

    
    // public void setName(String name) {
    //     this.name = name;
    // }

    // public void setBInitial(Boolean bInitial) {
    //     this.bInitial = bInitial;
    // }
    
    // public void setBFinal(Boolean bFinal) {
    //     this.bFinal = bFinal;
    // }

    // public void setTransitions(ArrayList<Transition> transitions) {
    //     this.transitions = transitions;
    // }


}
