public class Transition {
    State source;
    State dest;
    String input;

    public Transition (State state, State dest, String input) 
    {
        this.source = state;
        this.dest = dest;
        this.input = input;
    }

    public State getSource() {
        return this.source;
    }

    public void setSource(State source) {
        this.source = source;
    }

    public State getDest() {
        return this.dest;
    }

    public void setDest(State dest) {
        this.dest = dest;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    
}
