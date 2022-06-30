import java.util.*;
import java.io.*;

/**
 *  A machine project that checks the equivalence of N machines based on
 *  their states and transitions in the context of finite-state automata.
 *  @author Clyla A. Rafanan <>
 *  @author Kriz Royce A. Tahimic <>
 *  @author Erik Lance L. Tiongquico <eriklance@gmail.com>
 *  @author Faith Juliamae O. Griffin <fjo.griffin@gmail.com>
 */
public class Main {
    Model model;

    //View view; // it's okay to erase this you can use View.phase1Print() or View.phase2Print()
    public static void main(String[] args) throws IOException {
        //True if phase 1, false if phase 2
        boolean bPhase = true;
        ArrayList<Machine> machine = new ArrayList<Machine>();
        boolean bEquivalent;
        String input, mName, mInitial, mTrans, mState, mFinal;
        int numMachines, nStates, nInputs, nTrans, nFinals;
        int i, j, k, l, m, n, o, p;
        Fixer fix = new Fixer();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        // // ERIK TEST MACHINES
        // Machine firstM = new Machine("M1", 4);
        // ArrayList<State> m1States = new ArrayList<State>();
        // m1States.add(new State("A",true, false));
        // m1States.add(new State("B", false, true));
        //
        // // A trans
        // m1States.get(0).makeTransition(m1States.get(0), "0");
        // m1States.get(0).makeTransition(m1States.get(1), "1");
        //
        // // B trans
        // m1States.get(1).makeTransition(m1States.get(0), "0");
        // m1States.get(1).makeTransition(m1States.get(1), "1");
        //
        // firstM.setStates(m1States);
        //
        // // Machine 2 ------------------------------------------------
        // Machine secondM = new Machine("M2", 6);
        //
        //
        // ArrayList<State> m2States = new ArrayList<State>();
        // m2States.add(new State("A", true, false));
        // m2States.add(new State("B", false, true));
        // m2States.add(new State("C", false, false));
        //
        // // A trans
        // m2States.get(0).makeTransition(m2States.get(0), "0");
        // m2States.get(0).makeTransition(m2States.get(1), "1");
        //
        // // B trans
        // m2States.get(1).makeTransition(m2States.get(2), "0");
        // m2States.get(1).makeTransition(m2States.get(1), "1");
        //
        // // C trans
        // m2States.get(2).makeTransition(m2States.get(0), "0");
        // m2States.get(2).makeTransition(m2States.get(1), "1");
        //
        // secondM.setStates(m2States);
        //
        // // Machine 3 ------------------------------------------------
        // Machine thirdM = new Machine("M3", 4);
        // ArrayList<State> m3States = new ArrayList<State>();
        // m3States.add(new State("A", true, true));
        // m3States.add(new State("B", false, true));
        // m3States.add(new State("C", false, false));
        //
        // // A trans
        // m3States.get(0).makeTransition(m3States.get(0), "0");
        // m3States.get(0).makeTransition(m3States.get(1), "0");
        // m3States.get(0).makeTransition(m3States.get(2), "1");
        //
        // // B trans
        // m3States.get(1).makeTransition(m3States.get(1), "0");
        // m3States.get(1).makeTransition(m3States.get(1), "1");
        // m3States.get(1).makeTransition(m3States.get(2), "1");
        //
        // // C trans
        // m3States.get(2).makeTransition(m3States.get(0), "1");
        // m3States.get(2).makeTransition(m3States.get(2), "1");
        //
        // thirdM.setStates(m3States);
        //
        // // ------------------------------------------------
        // ArrayList<String> in = new ArrayList<String>();
        // in.add("0");
        // in.add("1");
        // firstM.setInputs(in);
        // secondM.setInputs(in);
        // thirdM.setInputs(in);
        //
        // Machine fixed = fix.convertToDFA(thirdM);

        // View.phase1Print(fix.isEquivalent(firstM, secondM));

        /* ---- END OF TEST MACHINES ---- */



        if (bPhase){
          numMachines = 2;
        }
        else{
          input = buffer.readLine();
          numMachines = Integer.parseInt(input);
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
          for (l = 0; l < nTrans; l++)
          {
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

        //prints!
        // System.out.println("Machine Name: " + mName);
        // for (o = 0; o < allInputs.size(); o++){
        //  System.out.println("Input: " + allInputs.get(o));
        // }
        // for (o = 0; o < allStates.size(); o++){
        //  System.out.println(allStates.get(o));
        //  State currState = allStates.get(o);
        //  System.out.println(currState.getTransitions().size());
        //  // System.out.println(allStates.get(o).displayTransitions());
        //  for (p = 0; p < currState.getTransitions().size(); p++){
        //    // System.out.println(currState.getTransitions().get(p));
        //    System.out.println(allStates.get(p).displayTransitionsSimple());
        //
        //  }
        // }

        // System.out.println("[1]");
        machine.get(i).setStates(allStates);
        // System.out.println("[2]");
        machine.get(i).setInputs(allInputs);

        //check if NFA
        if (!machine.get(i).isMDFA()){
          // Machine mTemp = machine.get(i);
          machine.set(i, fix.convertToDFA(machine.get(i)));
        } 

        // System.out.println("[3]");
        // prints machine for debugging purposes - TO COMMENT OUT later
        // View.machinePrint(machine.get(i));

      }


      if(bPhase) View.phase1Print(fix.isEquivalent(machine.get(0), machine.get(1)));
      // else View.phase2Print(arrEquivalent);

      // after using View class, use .flush() to close buffered writer
      View.out.flush();
    }
}
