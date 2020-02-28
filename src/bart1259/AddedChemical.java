package bart1259;

/**
 * Represents a chemical that will be added some time during the simulation
 */
public class AddedChemical {

    private Chemical chemical;
    private double time;
    private double amount;

    /**
     * Creates a new added chemical
     * @param chemical the chemical that will be added
     * @param time the time at which the chemical will be added
     * @param amount the amount of the chemical that will be added
     */
    public AddedChemical(Chemical chemical, double time, double amount){
        this.chemical = chemical;
        this.time = time;
        this.amount = amount;
    }

    //Accessors

    public Chemical getChemical() {
        return chemical;
    }

    public double getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }
}
