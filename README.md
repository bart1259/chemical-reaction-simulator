# Chemical Equilibrium Simulator

![image](https://i.imgur.com/jMYo5Nj.png)
![image](https://i.imgur.com/CodqPBE.png)

## Features

This program supports:

- Simulating reversible chemical reactions with custom
    + Reactants
    + Products
    + Stoichiometric Coefficients
    + Forward Reaction Rate
    + Equilibrium Constant
- Adding chemicals during the experiment
- Importing and exporting simulations parameters
- Outputting concentrations over time as a spreadsheet
- Deciding which chemicals to graph and output

## Tutorial

#### How to run the program
- Download the .jar file from the releases page
- Run the jar file

#### How to make a custom simulation

##### Specifying Chemicals

In the chemicals text box, all the chemicals present in the simulation are listed. The name of the
 chemical does not matter and is only used to reference that specific chemical. Next to each
chemical the initial concentration of the chemical can be listed, this can be any positive number
or zero. Afterwards, if a pound sign (#) is present, the chemical will be tracked.

###### Examples:

Nitrous oxide with an initial concentration of 3.0 M and which will NOT have its concentration
tracked.

    NO 3.0
    
Carbon dioxide which is not present at the beginning of the experiment and which will have its 
concentration be tracked.
    
    CarbonDioxide #

#####S pecifying Reversible Chemical Reactions

In the reactions text box, all the reaction the simulation obeys are listed. The first part of
the statement specifies the reactants and products with their respective stoichiometric 
coefficients. If no coefficient is present, a coefficient of 1 is assumed. Non-integer coefficients
are not supported. Following a semi colon, the forward rate constant is specified and following 
another semi colon, the equilibrium constant is specified.

###### Examples

A reaction of carbon monoxide and a methyl group to make an acetyl group. The forward reaction
rate constant is 15 and the equilibrium constant is 0.02.

    CO + CH3 -> CH3CO ; Kfwd = 15.0 ; Kequ = 0.02
    
A reaction of one part sulfuric acid and two parts sodium hydroxide to make one part sodium
sulfate and 2 parts water. The forward reaction rate constant is 6.0 and the equilibrium constant
is 5 x 10^7
    
    H2SO4 + 2 NaOH -> Na2SO4 + 2 H2O ; Kfwd = 6.0 ; Kequ = 5.0e7
    
##### Specifying Chemical Additions

In the chemical additions text box, all the additions that occur during an experiment are
stated. First the chemical that is to be added is stated. Next, the amount of the chemical, in
 moles per liter, to add or subtract is specified. This number can be negative (representing
 removing a chemical from the simulation). Next the time when the chemical is added is 
 specified. The time is measured in seconds.

###### Examples

3.0 moles per liter of oxygen gas will be added to the simulation 0.1 seconds into the experiment.

    O2 3.0 t = 0.1
    
4.0 moles per liter of carbon dioxide will be removed from the simulation 0.25 seconds into the
experiment. If less than 4.0 moles are present at 0.25 seconds, then all the carbon dioxide gas
will be removed.
    
    CO2 -4.0 t = 0.25

#### How to output the experiment as a spreadsheet

- Ensure the chemicals that need to be tracked have a # symbol next to them
- Run the simulation
- Go to the output tab and press the Output as CSV button

## Theory

A reversible chemical reaction is a reaction where reactants turn into products and products 
can turn back into reactants. Two reactions occur simultaneously, the forward and reverse 
reaction. Assume the general equation

    aA + bB <-> cC + dD

where A and B are the reactants, with a and b being their respective stoichiometric coefficients,
 and C and D are the products, with c and d being their respective stoichiometric coefficients. 
 According to the law of mass action, the rate of forward reaction is proportional to the 
 concentrations of the reactants raised to the power of their respective stoichiometric 
 coefficients. Therefore the rate of forward reaction would be
 
     kfwd * [A]^a * [B]^b

where kfwd is the forward reaction rate constant. 

The rate of the reverse reaction is proportional to
the concentrations of the products raised to their respective stoichiometric coefficients. So
the rate of the reverse reaction would be

    kbwd * [C]^c * [D]^d
 
where kbwd is the backward reaction rate constant.

Equilibrium occurs when the rate of forward reaction exactly equals the rate of the backward
reaction. This equilibrium position is usually represented as the equilibrium constant which is
equal to the forward rate constant divided by the backward rate constant.

       kfwd * [A]^a * [B]^b = kbwd * [C]^c * [D]^d
       
       kequ = kfwd / kbwd = ([C]^c * [D]^d) / ([A]^a * [B]^b)
       
The values of kfwd and kequ are unique for each reversible reaction.

## How the simulation works

By keeping track of all the chemicals present in the simulation and by knowing what chemical 
reactions can occur between the chemicals, with the constant values given, the forward reaction 
rate and backward reaction rates can be computed for an instant in time. If the desired outcome
is to get a completely accurate value for the concentrations, then a differential equation must
be solved. However, euler's method can be used to estimate the concentrations over time. By 
multiplying the rate, the units being moles per liter seconds, by a time value, the change in
 the molar concentration can be calculated for that time value. This can be repeated until the
 desired duration is reached. The lower the value for this time value, typically referred to as
 delta time, the more accurate the simulation will be but the longer it will take to compute.


## Potential future features

- Add support for temperature and pressure
- Add support Support heterogeneous reactions
- Support exporting graph as an image
- Make simulation run on separate thread and display progress bar of simulation to prevent the GUI from stalling
- Constant input and output of chemicals
