package bart1259;

import java.util.*;

/**
 * A class that represents a chemical simulation ina medium
 */
public class Simulation {

    private HashMap<Chemical, Double> chemicalAmounts;
    private ArrayList<ChemicalReaction> reactions;
    private ArrayList<AddedChemical> chemicalAdditions;
    private double timeSimulated = 0.0;

    /**
     * Creates a simulation
     */
    public Simulation(){
        //Initialize variables
        chemicalAmounts = new HashMap<>();
        reactions = new ArrayList<>();
        chemicalAdditions = new ArrayList<>();
    }

    /**
     * Simulate the chemical reactions of a duration of delta time
     * @param deltaTime the length of time to linearly simulate the reactions for
     */
    public void simulate(double deltaTime) {

        //Make a hash map of chemical changes
        //This way previous chemical reactions don't affect later ones that should
        //be simulated at the same time
        HashMap<Chemical, Double> changeAmounts = new HashMap<>();
        //Initialize the changes to zero
        for(Chemical c : chemicalAmounts.keySet()){
            changeAmounts.put(c,0.0);
        }

        //Loop through all the reactions and simulate them
        for(ChemicalReaction rxn : reactions) {

            //Calculate the rate of the forward reaction
            double fwdReaction = rxn.getFwdReactionRate();
            for (ChemicalComponent reactant : rxn.getReactants()){
                fwdReaction *= Math.pow(chemicalAmounts.get(reactant.getChemical()), reactant.getStoicheometricCoefficient());
            }

            //Calculate the rate of the backward reaction
            double bwdReaction = rxn.getBwdReactionRate();
            for (ChemicalComponent product : rxn.getProducts()){
                bwdReaction *= Math.pow(chemicalAmounts.get(product.getChemical()), product.getStoicheometricCoefficient());
            }

            //Calculate the overall reaction rate
            double reactionRate = fwdReaction - bwdReaction;

            //Subtract the reactants ( reactants could be added if the reaction rate is negative )
            for (ChemicalComponent reactant : rxn.getReactants()){
                double deltaChemical = -deltaTime * (reactionRate * reactant.getStoicheometricCoefficient());
                changeAmounts.put(reactant.getChemical(), deltaChemical + changeAmounts.get(reactant.getChemical()));
            }
            //Add to the products ( products could be removed if the reaction rate is negative )
            for (ChemicalComponent product : rxn.getProducts()){
                double deltaChemical = deltaTime * (reactionRate * product.getStoicheometricCoefficient());
                changeAmounts.put(product.getChemical(), deltaChemical + changeAmounts.get(product.getChemical()));
            }


        }
        //Increment the amount of time simulated
        timeSimulated += deltaTime;

        //Check for chemical additions
        for (int i = chemicalAdditions.size() - 1; i >= 0; i--){
            AddedChemical addition = chemicalAdditions.get(i);
            if(timeSimulated > addition.getTime()){
                changeAmounts.put(addition.getChemical(), addition.getAmount() + changeAmounts.get(addition.getChemical()));
                chemicalAdditions.remove(addition);
            }
        }

        //Update chemical amounts
        for(Chemical c : chemicalAmounts.keySet()){
            addChemical(c, changeAmounts.get(c));
        }

    }

    /**
     * Get a list of all chemicals present in the simulation
     * @return
     */
    public ArrayList<Chemical> getChemicals(){
        ArrayList<Chemical> chemicals = new ArrayList<>();

        //Loop through all the chemical amounts and add the keys to a list
        for (Chemical key : chemicalAmounts.keySet()){
            chemicals.add(key);
        }

        return chemicals;
    }

    /**
     * Adds a chemical addition to the simulation
     * @param addition the addition to add
     */
    public void addChemicalAddition(AddedChemical addition) { chemicalAdditions.add(addition); }

    /**
     * Adds a reaction to the simulation
     * @param reaction the reaction to add
     */
    public void addReaction(ChemicalReaction reaction){
        reactions.add(reaction);
    }

    /**
     * Adds a chemical amount to the simulation
     * @param chemical the chemical to add
     * @param numberOfMoles the amount to add (or subtract if the value is negative)
     */
    public void addChemical(Chemical chemical, double numberOfMoles){


        //If chemical is present add the amount to it
        if(chemicalAmounts.containsKey(chemical)){
            Double amt = chemicalAmounts.get(chemical);
            amt += numberOfMoles;
            chemicalAmounts.put(chemical,amt);
        }else{
            //If the chemical is not present add it to the hashmap
            chemicalAmounts.put(chemical, numberOfMoles);
        }

        //Ensure there is not a negative number present of a chemical
        if(chemicalAmounts.get(chemical) < 0){
            chemicalAmounts.put(chemical, 0.0);
        }

    }

    /**
     * Returns the concentration of a chemical in the simulation
     * @param chemical the chemical
     * @return the current concentration of the chemical
     */
    public double getConcentration(Chemical chemical){


        if(chemicalAmounts.containsKey(chemical)){
            //If chemical is present return the amount
            return chemicalAmounts.get(chemical);
        } else {
            //Return 0 if the chemical doesn't exist
            return 0.0;
        }

    }

}
