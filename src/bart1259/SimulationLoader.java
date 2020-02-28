package bart1259;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A class that loads the simulation
 */
public class SimulationLoader {

    /**
     * Parses a chemical reaction from a string of chemicals, reactions and chemical additions
     * @param chemicals string containing chemicals
     * @param reactions string containing reactions
     * @param additions string containing additions
     * @return the parsed simulation
     */
    public static Simulation parseSimulation(String chemicals, String reactions, String additions){
        Simulation simulation = new Simulation();

        parseChemicals(chemicals, simulation);
        parseReactions(reactions, simulation);
        parseAdditions(additions, simulation);

        return simulation;
    }

    /**
     * Converts the text into a list of chemical reactions
     * @param reactionText the text that contains the list of reactions
     * @param simulation the simulations to add the reactions to
     */
    private static void parseReactions(String reactionText, Simulation simulation) {

        Scanner scanner = new Scanner(reactionText);
        String line = "";

        int chemicalReactionNumber = 1;
        //Loop through each reaction
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            if(line.isEmpty()){
                continue;
            }
            //Parse and then add the reaction to the simulation
            simulation.addReaction(parseChemicalReaction(line, simulation.getChemicals(), chemicalReactionNumber));
            chemicalReactionNumber++;
        }

    }

    /**
     * Converts text into a list of chemicals
     * @param chemicalText the text that contains the list of chemicals
     * @param simulation the simulation to add the chemicals to
     * @return a list of all the chemicals in the text
     */
    private static List<Chemical> parseChemicals(String chemicalText, Simulation simulation){

        ArrayList<Chemical> chemicals = new ArrayList<>();

        Scanner scanner = new Scanner(chemicalText);
        String line = "";
        int chemicalIndex = 1;

        //Loop through every line
        while(scanner.hasNextLine()){

            line = scanner.nextLine();

            //If the line is empty, skip it
            if(line.isEmpty()){
                continue;
            }

            Scanner lineScanner = new Scanner(line);

            //Ensure name of chemical is not a number
            if(lineScanner.hasNextDouble()){
                throw new RuntimeException("chemical # " + chemicalIndex + ", cannot have the name of a number number: " + lineScanner.nextDouble());
            }

            //Get the name of the chemical
            String chemicalName = lineScanner.next();
            double intialAmount = 0.0;

            //Try and get the initial amount of the chemical
            if(lineScanner.hasNextDouble()){
                intialAmount = lineScanner.nextDouble();
                if(intialAmount < 0){
                    throw new RuntimeException("chemical # " + chemicalIndex + " cannot have initial chemical amount less than 0: " + intialAmount);
                }
            }

            //Ensure two identical chemicals cannot exist
            for(Chemical c : chemicals){
                if(c.getChemicalName().equals(chemicalName)){
                    throw new RuntimeException("chemical # " + chemicalIndex + " has a name identical to that of another chemical");
                }
            }

            //Make the chemical and add it to the simulation
            Chemical chemical = new Chemical(chemicalName);
            simulation.addChemical(chemical, intialAmount);
            chemicals.add(chemical);

            //Keep track of which chemical is being parsed
            chemicalIndex++;

        }

        return chemicals;
    }

    /**
     * Get which chemicals should be tracked
     * @param chemicalText the text that contains the list of chemicals
     * @param chemicals the possible chemicals to be tracked
     * @return a list of chemicals that should be tracked
     */
    public static List<Chemical> getTrackedChemicals(String chemicalText, List<Chemical> chemicals){

        ArrayList<Chemical> trackedChemicals = new ArrayList<>();

        Scanner scanner = new Scanner(chemicalText);
        String line = "";
        int chemicalIndex = 1;

        //Loop through every line
        while(scanner.hasNextLine()){

            line = scanner.nextLine();

            //If the line is empty, skip it
            if(line.isEmpty()){
                continue;
            }

            Scanner lineScanner = new Scanner(line);

            //Get the name of the chemical
            String chemicalName = lineScanner.next();

            //Get the last token
            String token = "";
            while(lineScanner.hasNext()){
                token = lineScanner.next();
            }

            //If last token is #, then the chemical should be tracked
            if(token.equals("#")){
                for (Chemical c : chemicals){
                    if(c.getChemicalName().equals(chemicalName)){
                        trackedChemicals.add(c);
                        break;
                    }
                }
            }

        }

        return trackedChemicals;
    }

    /**
     * Parse a line into a chemical reaction
     * @param line the line that represents the chemical reaction
     * @param chemicals the chemicals that could be involved with the reaction
     * @param index the index of the chemical reaction for error message generation
     * @return the parsed chemical reaction
     */
    private static ChemicalReaction parseChemicalReaction(String line, List<Chemical> chemicals, int index) {

        //Make a scanner for line in order to be able to break the line up into tokens
        Scanner lineScanner = new Scanner(line);
        ArrayList<ChemicalComponent> reactants = new ArrayList<>();
        ArrayList<ChemicalComponent> products = new ArrayList<>();

        int coefficient = 1;
        boolean parsingReactants = true;

        //Default k fwd and k equ values
        double kfwd = -1.0;
        double kequ = -1.0;

        do {
            if(lineScanner.hasNextInt()){
                coefficient = lineScanner.nextInt();
            }else {
                String token = lineScanner.next();

                //If an arrow is present, the next chemicals to be parsed will be products
                if(token.equals("->")){
                    parsingReactants = false;
                    continue;
                }

                //Get past useless tokens
                if(token.equals("+") || token.equals(";")){
                    continue;
                }

                if(token.equals("Kfwd")){
                    //Get past equals
                    if(lineScanner.hasNext()){
                        lineScanner.next();
                    }

                    //Ensure next value is a number
                    if(!lineScanner.hasNextDouble()){
                        throw new RuntimeException("reaction # " + index + " has no Kfwd value. Missing an equals sign?");
                    }

                    //Get value of forward reaction coefficient
                    kfwd = lineScanner.nextDouble();
                    continue;
                }

                if(token.equals("Kequ")){
                    //Get past equals
                    if(lineScanner.hasNext()){
                        lineScanner.next();
                    }

                    //Ensure next value is a number
                    if(!lineScanner.hasNextDouble()){
                        throw new RuntimeException("reaction # " + index + " has no Kequ value. Missing an equals sign?");
                    }

                    //Get value of equilibrium
                    kequ = lineScanner.nextDouble();
                    continue;
                }

                //We know token is a chemical
                Chemical chemical = null;
                for (Chemical c : chemicals){
                    if(c.getChemicalName().equals(token)){
                        chemical = c;
                        break;
                    }
                }

                //Ensure a chemical exists
                if(chemical == null){
                    throw new RuntimeException("reaction # " + index + " has unknown chemical " + token);
                }

                //Add chemical component to correct side
                if(parsingReactants){
                    reactants.add(new ChemicalComponent(chemical, coefficient));
                }else{
                    products.add(new ChemicalComponent(chemical, coefficient));
                }

                //Reset coefficient to 1 now that the chemical has been parsed
                coefficient = 1;

            }

        } while (lineScanner.hasNext());

        //Ensure fwd reaction values and equilibrium values were assigned
        if(kfwd <= 0 || kequ <= 0){
            throw new RuntimeException("reaction # " + index + " has no valid kfwd and/or kequ");
        }

        //Make the chemical reaction
        ChemicalReaction rxn =  new ChemicalReaction(kfwd,kequ);

        for(ChemicalComponent reactant : reactants){
            rxn.addReactants(reactant);
        }
        for(ChemicalComponent product : products){
            rxn.addProducts(product);
        }

        return rxn;
    }

    /**
     * Parses chemical additions from text
     * @param additionsText the text that contains the additions data
     * @param simulation the simulation to add the additions to
     */
    private static void parseAdditions(String additionsText, Simulation simulation) {
        Scanner scanner = new Scanner(additionsText);
        String line = "";

        int index = 1;

        //Go through all the lines of the additions
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            //Skip the line if it is empty
            if(line.isEmpty()){
                continue;
            }
            //Add a the chemical to the list of additions
            simulation.addChemicalAddition(parseChemicalAddition(line, simulation.getChemicals(), index));
            index++;
        }
    }

    /**
     * Parses a chemical addition from a line of text
     * @param line the line that represents the chemical addition
     * @param chemicals the chemicals that could be involved in the addition
     * @param index the index of the chemical reaction for error message generation
     * @return the parsed chemical addition
     */
    private static AddedChemical parseChemicalAddition(String line, List<Chemical> chemicals, int index) {

        //Make a scanner for the line that will allow the line to be broken up token by token
        Scanner lineScanner = new Scanner(line);

        double time = 0.0;
        double amount = 0.0;
        Chemical chemical = null;

        do {
            String token = lineScanner.next();

            if(token.equals("t")){
                //Get past equals
                if(lineScanner.hasNext()){
                    lineScanner.next();
                }
                //Ensure next value is number
                if(!lineScanner.hasNextDouble()){
                    throw new RuntimeException("addition # " + index + " has no time value. Missing an equals sign?");
                }
                //Get value of forward reaction coefficient
                time = lineScanner.nextDouble();
                continue;
            }

            //We know token is a chemical
            for (Chemical c : chemicals){
                if(c.getChemicalName().equals(token)){
                    chemical = c;
                    break;
                }
            }

            //If the chemical cannot be found throw an error
            if(chemical == null){
                throw new RuntimeException("addition # " + index + " has no valid chemical");
            }

            //Ensure there is a valid addition amount
            if(!lineScanner.hasNextDouble()){
                throw new RuntimeException("addition # " + index + " has no valid addition amount");
            }
            amount = lineScanner.nextDouble();
        } while (lineScanner.hasNext());

        return new AddedChemical(chemical, time, amount);

    }

    /**
     * Gets the simulation variables from a file
     * @param path the path to the file
     * @return an array of the variables 0 - chemicals ; 1 - reactions ; 2 - additions
     */
    public static String[] getSimulationVariables(String path){

        String[] variables = new String[3];

        try(Scanner fileScanner = new Scanner(new File(path))){
            String line = "";
            while (!line.trim().toUpperCase().equals("~CHEMICALS")){
                line = fileScanner.nextLine();
            }

            String chemicalDefinitionsText = "";
            line = fileScanner.nextLine();

            //parse chemicals
            while (!line.trim().toUpperCase().equals("~REACTIONS")){
                if(line.isEmpty()){
                    line = fileScanner.nextLine();
                    continue;
                }
                chemicalDefinitionsText += line + "\r\n";
                line = fileScanner.nextLine();
            }

            String chemicalReactionsText = "";
            line = fileScanner.nextLine();

            //make reaction list
            do{
                if(line.isEmpty()){
                    line = fileScanner.nextLine();
                    continue;
                }

                chemicalReactionsText += line + "\r\n";
                line = fileScanner.nextLine();

            } while(fileScanner.hasNextLine() && !line.trim().toUpperCase().equals("~ADDITIONS"));

            String chemicalAdditionsText = "";

            //parse chemical additions
            do{
                line = fileScanner.nextLine();
                if(line.isEmpty()){
                    continue;
                }

                chemicalAdditionsText += line + "\r\n";
            }while(fileScanner.hasNextLine());

            //Prepare the variable array
            variables[0] = chemicalDefinitionsText;
            variables[1] = chemicalReactionsText;
            variables[2] = chemicalAdditionsText;

        } catch (FileNotFoundException e){
            throw new RuntimeException("File not found " + e.getMessage());
        } catch (NoSuchElementException e){
            throw new RuntimeException("Unexpected text in simulation file, File may be corrupt");
        }

        return variables;
    }
}
