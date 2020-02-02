# Bayesian Network Probability Computations

This is a final project for Algorithmic decision making course.

## General overview
Bayesian Network is a simple, graphical notation for conditional independence assertions and hence for compact specification of full joint distributions.
This project consists of the implementation of a Bayesian Network system and calculation of propabilties based on the following 3 ways:

1. Simple probabilty calculation based on basic P( b|j, m ) = P(b,j,^m )/ P(j,^m)
2. Calculation using Variable elimination algorithm while removing not relevent variables at the begging while elimination order is by ABC.
3. Same as above while the elimination order is diffrent in a way that we eliminate first the varible with least children nodes and thus making the algorithms efficiantcy slightly better.

## Implementation

The input file is a simple text file in the following form:
first describing the Baysian network:
  1. The varriables in the network.
  2. The values each variable can receive.
  3. The parent node of each variable.
  4. The conditional propablity table(CPT) for each varriable.
  
Then at the end the queries we wish to calculate in the following way:
The query and the algorithm for calculation(1 for Simple probabilty calculation, 2 for Variable elimination etc.)

(See example of input files in "input examples" folder)

The program will create an output.txt file with the answers in the follwoing form:
  1. each line is for each query.
  2. first the answer will show then the ammount of addition actions (+) and then ammount of multiplication actions (*)
  for example:
  0.28417,7,32
  0.28417,7,16
  ...
  
## How to run:

first navigate to bin folder
```
cd bin
```
create a input.txt file as instructed above(or by using given examples) in bin directory.
Run following command to run program:
```
java Main input.txt output.txt
```
This above command will take your input.txt created, run the program and algorithms chosen on it and create output.txt with the answers.


