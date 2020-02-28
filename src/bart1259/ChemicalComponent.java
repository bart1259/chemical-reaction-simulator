package bart1259;

/**
 * Represents a chemical component in a chemical reaction
 */
public class ChemicalComponent {

    private Chemical chemical;
    private int stoicheometricCoefficient;

    /**
     * Creates a new chemical component
     * @param chemical the chemical
     * @param stoicheometricCoefficient how much of the chemical is presentin the reaction
     */
    public ChemicalComponent(Chemical chemical, int stoicheometricCoefficient){
        this.chemical = chemical;
        this.stoicheometricCoefficient = stoicheometricCoefficient;
    }

    //Accessors

    public Chemical getChemical() {
        return chemical;
    }

    public int getStoicheometricCoefficient() {
        return stoicheometricCoefficient;
    }
}
