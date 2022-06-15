public class Transition {
    private State source;
    private State dest;
    private String input;

    public Transition(State source, State dest, String input) {
        this.source = source;
        this.dest = dest;
        this.input = input;
    }
    
    // Auto-generated getters and setters
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

    @Override
    public String toString()
    {
        // I did not include the transitions attribute 
        // As I thought it might made it confusing 
        //(I reserved it to be include in State toString)
        String strSource =  " source: +"+
                            "\n  name: " + source.getName() +
                            "\n  bInitial: "+ source.isBInitial()+
                            "\n  bFinal"+ source.isBFinal()+
                            "\n";

        String strDest = " dest: "+ 
                        "\n  name: " + dest.getName() +
                        "\n  bInitial: "+ dest.isBInitial()+
                        "\n  bFinal"+ dest.isBFinal()+
                        "\n";

        return  "{"+ strSource + 
                "input: " + this.input + "\n" + 
                strDest +
                "}"; 
    }
}
