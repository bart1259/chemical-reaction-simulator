package bart1259;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 * A class that controls what the UI does
 */
public class Controller {

    //Graph variables
    @FXML
    private LineChart<Number,Number> concentrationChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    //Text fields for experiment settings
    @FXML
    private TextField deltaTimeTextField;
    @FXML
    private TextField experimentDurationTextField;

    //Text fields for experiment parameters
    @FXML
    private TextArea chemicalsTextBox;
    @FXML
    private TextArea reactionsTextBox;
    @FXML
    private TextArea additionsTextBox;

    /**
     * Initializes the graph and text boxes of the UI
     */
    public void initialize(){
        xAxis.setLabel("Time (s)");
        xAxis.setAutoRanging(false);
        yAxis.setLabel("Molarity (M)");
        yAxis.setForceZeroInRange(false);

        concentrationChart.setAnimated(false);
        concentrationChart.setCreateSymbols(false);

        //Set the default text box text
        chemicalsTextBox.setText("H2SO4 1.0 # \n" +
                "NaOH 1.0 # \n" +
                "Na2SO4 \n" +
                "H2O #");
        reactionsTextBox.setText("H2SO4 + 2 NaOH -> Na2SO4 + 2 H2O ; Kfwd = 6.0 ; Kequ = 5.0e7");
        additionsTextBox.setText("NaOH 1.0 t = 0.5");
    }

    /**
     * Called when the start simulation button is pressed
     * @param event the event that triggered the button to be pressed
     */
    @FXML
    public void startSimulation(ActionEvent event) {


        //Parse delta time and the duration of the experiment
        double dt = 0.001;
        double duration = 1.0;
        try{
            dt = sanitizeDoubleInput(deltaTimeTextField, "Delta Time");
            duration = sanitizeDoubleInput(experimentDurationTextField, "Duration");
        } catch (RuntimeException e){

            displayError("Could not parse " + e.getMessage());
            return;
        }

        if (dt <= 0 || duration <= 0 || dt > duration) {
            displayError("Unreasonable values for dt and/or duration");
            return;
        }


        //Try parse and make the simulation
        try{
            Simulation simulation = SimulationLoader.parseSimulation(chemicalsTextBox.getText(), reactionsTextBox.getText(), additionsTextBox.getText());
            List<Chemical> trackedChemicals = SimulationLoader.getTrackedChemicals(chemicalsTextBox.getText(), simulation.getChemicals());

            //Clear chart
            concentrationChart.getData().clear();

            //Run the simulation
            runSimulation(simulation, duration, dt, trackedChemicals.toArray(new Chemical[trackedChemicals.size()]));

        } catch (RuntimeException exception){
            displayError("Error parsing the simulation: " + exception.getMessage());
        }

    }

    /**
     * Called when the open simulation file button is pressed
     * @param event the event that triggered the button to be pressed
     */
    @FXML
    private void openSimulationFile(ActionEvent event){
        //Prompt the user to select a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Simulation Files", "*.sim")
        );

        File file = fileChooser.showOpenDialog(null);

        //Check if user selected a file
        if(file != null){

            try{
                //Load the simulation variables from the file
                String[] variables = SimulationLoader.getSimulationVariables(file.getAbsolutePath());

                chemicalsTextBox.setText(variables[0]);
                reactionsTextBox.setText(variables[1]);
                additionsTextBox.setText(variables[2]);
            } catch (RuntimeException exception){
                displayError("Error during reading file: " + exception.getMessage());
            }
        }
    }

    /**
     * Called when the output csv button is pressed
     * @param event the event that triggered the button to be pressed
     */
    @FXML
    private void outputCSV(ActionEvent event) {

        //Ensure an experiment was run prior to outputting csv
        if (concentrationChart.getData().size() <= 0) {
            displayError("An experiment must be run before a csv can be outputted");
            return;
        }

        //Choose a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Spreadsheets", ".csv"));
        File file = fileChooser.showSaveDialog(null);

        //No file selected, return
        if(file == null){
            return;
        }

        //Prepare the CSV writer
        try(PrintWriter writer = new PrintWriter(file)){


            //Get the time row
            String csvText = "Time: ";

            for (int j = 0; j < concentrationChart.getData().get(0).getData().size(); j++){

                csvText += ",";
                csvText += concentrationChart.getData().get(0).getData().get(j).getXValue();
            }

            csvText += '\n';

            //Get the chemical molarity rows
            for (int i = 0; i < concentrationChart.getData().size(); i++) {

                String row = "";

                XYChart.Series<Number, Number> series = concentrationChart.getData().get(i);
                row += series.getName();

                for (int j = 0; j < series.getData().size(); j++){
                    row += ",";
                    row += series.getData().get(j).getYValue();
                }

                row += '\n';
                csvText += row;
            }

            //Write text to file
            writer.write(csvText);


            //Notify user spreadsheet was outputted
            Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
            successAlert.setHeaderText("Success!");
            successAlert.setContentText("Successfully outputted file " + file.getAbsolutePath());
            successAlert.showAndWait();

        } catch (FileNotFoundException exception){
            displayError("Error while writing  " + file.getAbsolutePath());
        }
    }

    /**
     * Called when the save simulation file button is pressed
     * @param event the event that triggered the button to be pressed
     */
    @FXML
    private void saveSimulationFile(ActionEvent event){
        //Choose a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation Files", ".sim"));
        File file = fileChooser.showSaveDialog(null);

        //No file selected, return
        if(file == null){
            return;
        }

        //Prepare the simulation writer
        try(PrintWriter writer = new PrintWriter(file)){

            writer.write("~Chemicals\r\n");
            writer.write(chemicalsTextBox.getText().replace("\n","\r\n"));
            writer.write("\r\n~Reactions\r\n");
            writer.write(reactionsTextBox.getText().replace("\n","\r\n"));
            writer.write("\r\n~Additions\r\n");
            writer.write(additionsTextBox.getText().replace("\n","\r\n"));

            //Notify user spreadsheet was outputted
            Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
            successAlert.setHeaderText("Success!");
            successAlert.setContentText("Successfully outputted file " + file.getAbsolutePath());
            successAlert.showAndWait();

        } catch (FileNotFoundException exception){
            displayError("Error while writing to " + file.getAbsolutePath());
        }
    }

    /**
     * Runs the simulation
     * @param simulation the simulation to run
     * @param duration how long to run the simulation for
     * @param deltaTime the accuracy of the simulation (lower the more accurate)
     * @param trackedChemicals a list of chemicals that should be tracked
     */
    private void runSimulation(Simulation simulation, double duration, double deltaTime, Chemical... trackedChemicals){

        //Make a hash map of all the chemicals that are going to be tracked with there data series
        HashMap<Chemical,XYChart.Series> chemicalSeries = new HashMap<>();

        //Initialize all the series
        for (Chemical c : trackedChemicals){
            XYChart.Series series = new XYChart.Series();
            series.setName(c.getChemicalName());
            chemicalSeries.put(c, series);
        }

        //Run through the simulation through the duration (plus a little due to floating point precision)
        for (double t = 0; t <= duration + (deltaTime/2.0); t += deltaTime) {

            //Simulate
            simulation.simulate(deltaTime);

            //Update the chemicals
            for (Chemical c : trackedChemicals){
                chemicalSeries.get(c).getData().add(new XYChart.Data<>(t,simulation.getConcentration(c)));
            }

        }

        //Set the x axis properties to display all the data
        xAxis.setUpperBound(duration);
        xAxis.setTickUnit(duration / 10.0);
        xAxis.setMinorTickCount(5);

        //Add all the chemicals to the graph
        for (Chemical c : trackedChemicals){
            concentrationChart.getData().add(chemicalSeries.get(c));
        }
    }

    /**
     * Sanatizes double from text field input
     * @param textField text field to get the double from
     * @param inputName the name of the value retrieving (only for exception purposes)
     * @return the value from the text field
     */
    private double sanitizeDoubleInput(TextField textField, String inputName) {
        try{
            //Attempt to convert the string to a double
            return Double.parseDouble(textField.getText());
        } catch (Exception e){
            //If something went wrong, throw an error
            throw new RuntimeException(inputName + " value: " + textField.getText());
        }

    }

    /**
     * Display an error with an error message of errorMessage
     * @param errorMessage the error message
     */
    private void displayError(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

}
