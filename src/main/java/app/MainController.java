package app;

import com.fazecast.jSerialComm.SerialPort;
import data.DataHolder;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private Stage window;

    @FXML
    public Button startStopScanCANBusViaComPortButton;
    @FXML
    public Button startScanCANDataFileButton;

    @FXML
    private Button refreshComPortsButton;
    @FXML
    public ComboBox<String> existedComPortsComboBox;

    private SerialPort[] commPorts;

    List<DataHolder> dataHolders = new ArrayList<>();
    @FXML
    public TableView<DataHolder> canBusDataTableView;
    @FXML
    public TableColumn<DataHolder, String> canIdTableColumn;
    @FXML
    public TableColumn<DataHolder, String> lengthTableColumn;
    @FXML
    public TableColumn<DataHolder, String> canDataTableColumn;
    @FXML
    public TableColumn<DataHolder, String> frequencyCounterTableColumn;
    @FXML
    public TableColumn<DataHolder, String> deviceNameTableColumn;
    @FXML
    public TableColumn<DataHolder, String> deviceCanAreaTableColumn;
    @FXML
    public TableColumn<DataHolder, ComboBox<String>> uniqueCanMessagesTableColumn;

    @FXML
    public Button canDataSendButton;
    @FXML
    public TextArea canIdTextArea;
    @FXML
    public TextArea byteZeroTextArea;
    @FXML
    public TextArea byteOneTextArea;
    @FXML
    public TextArea byteTwoTextArea;
    @FXML
    public TextArea byteThreeTextArea;
    @FXML
    public TextArea byteFourTextArea;
    @FXML
    public TextArea byteFiveTextArea;
    @FXML
    public TextArea byteSixTextArea;
    @FXML
    public TextArea byteSevenTextArea;

    private TextArea[] textAreasHexBytes;

    private int lengthOfHexData = 0;


    //== methods for initialisation main controller ====================================================================
    public void initialize() {
        existedComPortsComboBox.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    String pickedComPortName = existedComPortsComboBox.getSelectionModel().getSelectedItem();
                    LOGGER.log(Level.INFO, "COM-port selected : " + pickedComPortName);
                });

        canIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("deviceId")); //mapped to DataHolder.getDeviceId()
        lengthTableColumn.setCellValueFactory(new PropertyValueFactory<>("lengthOfCanData"));//mapped to DataHolder.getLengthOfCanData()
        canDataTableColumn.setCellValueFactory(new PropertyValueFactory<>("stringCanData"));//mapped to DataHolder.getStringCanData()
        frequencyCounterTableColumn.setCellValueFactory(new PropertyValueFactory<>("frequencyCounter"));//mapped to DataHolder.getFrequencyCounter()
        deviceNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));//mapped to DataHolder.getDeviceName()
        deviceCanAreaTableColumn.setCellValueFactory(new PropertyValueFactory<>("deviceCanArea"));//mapped to DataHolder.getDeviceCanArea()
        uniqueCanMessagesTableColumn.setCellValueFactory(param -> { //custom mappings
            final Set<String> uniqueCanMessages1 = param.getValue().getUniqueCanMessages();
            final ObservableList<String> uniqueCanMessages = FXCollections.observableArrayList(uniqueCanMessages1);
            final ComboBox<String> langsComboBox = new ComboBox<>(uniqueCanMessages);
            langsComboBox.setValue(uniqueCanMessages1.iterator().next()); // устанавливаем выбранный элемент по умолчанию
//            Label lbl = new Label();
//            // получаем выбранный элемент
//            langsComboBox.setOnAction(event -> lbl.setText(langsComboBox.getValue()));
            return new ReadOnlyObjectWrapper<>(langsComboBox);
        });
        comPortCanScanner = new ComPortCanScanner(
                startStopScanCANBusViaComPortButton,
                existedComPortsComboBox,
                canBusDataTableView
        );
        textAreasHexBytes = new TextArea[]{
                byteZeroTextArea,
                byteOneTextArea,
                byteTwoTextArea,
                byteThreeTextArea,
                byteFourTextArea,
                byteFiveTextArea,
                byteSixTextArea,
                byteSevenTextArea
        };
    }

    public void setMainStage(final Stage primaryStage) {
        window = primaryStage;
    }
    //==================================================================================================================

    public void refreshComPortsButtonClicked(final ActionEvent actionEvent) {
        commPorts = SerialPort.getCommPorts();

        final List<String> comPortsDescriptions = Arrays.stream(commPorts)
                .map(serialPort -> serialPort.getSystemPortName() + " " + serialPort.getPortDescription())
                .collect(Collectors.toList());

        existedComPortsComboBox.getItems().clear();
        existedComPortsComboBox.getItems().addAll(FXCollections.observableArrayList(comPortsDescriptions));
        comPortCanScanner.setCommPorts(commPorts);
    }


    ComPortCanScanner comPortCanScanner;

    public void startStopScanCANBusViaComPortButtonClicked(final ActionEvent actionEvent) {
        comPortCanScanner.startStopScanCANBusViaComPortButtonClicked();
    }

    FileCanDataScanner fileCanDataScanner;

    public void startScanCANDataFileButtonClicked(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(window);
        if (fileCanDataScanner == null) {
            fileCanDataScanner = new FileCanDataScanner(dataHolders, canBusDataTableView);
        }
        if (file != null) {
            try {
                fileCanDataScanner.openFileAndFillTable(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCanDataFromHexFields() {

        for (int i = 0; i < textAreasHexBytes.length; i++) {
            String hexNumberAsText = textAreasHexBytes[i].getText();
            if (!hexNumberAsText.isEmpty()) {
                lengthOfHexData += 1;
            }
        }

        int[] hexNumbersToSend = new int[lengthOfHexData + 2];
        hexNumbersToSend[0] = Integer.parseInt(canIdTextArea.getText(), 16);
        hexNumbersToSend[1] = lengthOfHexData;

        for (int i = 0; i < textAreasHexBytes.length; i++) {
            String hexNumberAsText = textAreasHexBytes[i].getText();
            if (!hexNumberAsText.isEmpty()) {
                hexNumbersToSend[i + 2] = Integer.parseInt(hexNumberAsText, 16);
            }
        }

        comPortCanScanner.writeCanDataToCommPort(hexNumbersToSend);
    }


}
