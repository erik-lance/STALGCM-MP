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

    public Boolean isMDFA (){
        Boolean out = false;

        // TO DO

        return out;
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
