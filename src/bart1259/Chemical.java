package bart1259;

/**
 * Represents a chemical
 */
public class Chemical {

    private String chemicalName;

    /**
     * Creates a chemical
     * @param chemicalName the name of the chemical
     */
    public Chemical (String chemicalName){
        this.chemicalName = chemicalName;
    }

    //Accessors

    public String getChemicalName() {
        return chemicalName;
    }
}
