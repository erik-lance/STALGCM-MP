import java.util.ArrayList;

public class Machine {
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

    public Boolean isMDFA (){
        Boolean out = false;

        // TO DO

        return out;
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

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public void setTransitionNum(int TransitionNum) {
    //     this.TransitionNum = TransitionNum;
    // }

    // public void setStates(ArrayList<State> states) {
    //     this.states = states;
    // }

    // public void setInputs(ArrayList<String> inputs) {
    //     this.inputs = inputs;
    // }

    



    
    
}
