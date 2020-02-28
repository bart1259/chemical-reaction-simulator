package bart1259;

import java.util.ArrayList;

/**
 * Represents a chemical reaction that turns reactants into products and
 * vice versa. The reaction has a forward reaction rate, and an equilibrium
 */
public class ChemicalReaction {

    private ArrayList<ChemicalComponent> reactants;
    private ArrayList<ChemicalComponent> products;

    private double fwdReactionRate;
    private double bwdReactionRate;
    private double equilibriumConstant;

    /**
     * Creates a new chemical reaction
     * @param fwdReactionRate the rate constant of the forward reaction
     * @param equilibriumConstant the equilibrium constant of the reaction
     */
    public ChemicalReaction(double fwdReactionRate, double equilibriumConstant){
        this.fwdReactionRate = fwdReactionRate;
        this.equilibriumConstant = equilibriumConstant;
        this.bwdReactionRate = fwdReactionRate / equilibriumConstant;

        //initialize lists
        reactants = new ArrayList<>();
        products = new ArrayList<>();
    }

    /**
     * Adds a chemical component as a reactant to the reaction
     * @param chemicalComponent the component to add as a reactant
     */
    public void addReactant(ChemicalComponent chemicalComponent){
        reactants.add(chemicalComponent);
    }

    /**
     * Adds chemical components as reactants to the reaction
     * @param chemicalComponents the components to add as reactants
     */
    public void addReactants(ChemicalComponent... chemicalComponents){
        for(ChemicalComponent c : chemicalComponents){
            addReactant(c);
        }
    }

    /**
     * Adds a chemical component as a product to the reaction
     * @param chemicalComponent the component to add as a product
     */
    public void addProduct(ChemicalComponent chemicalComponent){
        products.add(chemicalComponent);
    }

    /**
     * Adds chemical components as products to the reaction
     * @param chemicalComponents the components to add as products
     */
    public void addProducts(ChemicalComponent... chemicalComponents){
        for(ChemicalComponent c : chemicalComponents){
            addProduct(c);
        }
    }

    //Accessors


    public ArrayList<ChemicalComponent> getReactants() {
        return reactants;
    }

    public ArrayList<ChemicalComponent> getProducts() {
        return products;
    }

    public double getFwdReactionRate() {
        return fwdReactionRate;
    }

    public double getBwdReactionRate() {
        return bwdReactionRate;
    }

    public double getEquilibriumConstant() {
        return equilibriumConstant;
    }
}
