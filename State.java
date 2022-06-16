import java.util.ArrayList;

public class State {
    String name;
    Boolean bInitial;
    Boolean bFinal; // Changed into bFinal since final is a reserved token
    ArrayList<Transition> transitions;

    public State(String name, Boolean bInitial, Boolean bFinal) {
        this.name = name;
        this.bInitial = bInitial;
        this.bFinal = bFinal;
        this.transitions= new ArrayList<Transition>();
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", bInitial='" + isBInitial() + "'" +
            ", bFinal='" + isBFinal() + "'" +
            "}";
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
