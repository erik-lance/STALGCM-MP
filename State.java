import java.util.ArrayList;

public class State {
    String name;
    Boolean bInitial;
    Boolean bFinal; // Changed into bFinal since final is a reserved token
    ArrayList<Transition> transitions;

    public State(String name, Boolean bInitial, Boolean bFinal, ArrayList<Transition> transitions) {
        this.name = name;
        this.bInitial = bInitial;
        this.bFinal = bFinal;
        this.transitions = transitions;
    }

    

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isBInitial() {
        return this.bInitial;
    }

    public Boolean getBInitial() {
        return this.bInitial;
    }

    public void setBInitial(Boolean bInitial) {
        this.bInitial = bInitial;
    }

    public Boolean isBFinal() {
        return this.bFinal;
    }

    public Boolean getBFinal() {
        return this.bFinal;
    }

    public void setBFinal(Boolean bFinal) {
        this.bFinal = bFinal;
    }

    public ArrayList<Transition> getTransitions() {
        return this.transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }


}
