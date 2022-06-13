# STALGCM-MP
A Machine Project in Finite State Automata that checks the equivalence of N machines.

## Project
A machine is . . .



## Specs
All names (for machines, states) follows the regex /^[A-Za-z0-9]+$/.

<NFA Name>
|Q|
<|Q| lines follow, each with a state name>
|S|
<|S| lines follow, each with a stimulus symbol>
|δ|
<|δ|, each with a transition of the format <src> <stimulus> <dest>>
q_I - initial state (guaranteed to only have one)
|F|
<|F| lines follow, each with a final state, which is a valid member of Q from above>
