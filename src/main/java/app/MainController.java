package app;

import com.fazecast.jSerialComm.SerialPort;
import data.DataHolder;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private Stage window;

    @FXML
    public Button startScanCANBusViaComPortButton;
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
    }

    public void startScanCANBusViaComPortButtonClicked(final ActionEvent actionEvent) {
        final int selectedIndexOfComPort = existedComPortsComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndexOfComPort >= 0) {
            canBusDataTableView.getItems().clear();
            dataHolders.clear();
            //TODO 25/11/2020 lartsev: write data here
            SerialPort comPort = commPorts[selectedIndexOfComPort];
            comPort.openPort();
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            InputStream in = comPort.getInputStream();
            try
            {
                for (int j = 0; j < 1000; ++j)
                    System.out.print((char)in.read());
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            comPort.closePort();
        }
    }

    public void startScanCANDataFileButtonClicked(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try {
                openFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(File file) throws IOException {
        //19:08:46.657 -> 568 8 0 0 69 0 46 2F BB BB
        final Map<String, DataHolder> stringDataHolderMap = Files.lines(file.toPath())
                .map(line -> line
                        .split(" -> ")[1] //removing time label put by arduino logger
                        .split(" "))
                .collect(Collectors.toMap(
                        strings -> strings[0], //ID -> 568
                        strings -> {
                            try {
                                //System.out.println(Arrays.toString(strings));
                                final int lengthOfCanDataByCAN = Integer.parseInt(strings[1]); //8
                                final int lengthOfCanDataActual = strings.length - 2;
                                final String[] canData;
                                if (lengthOfCanDataByCAN == lengthOfCanDataActual) {
                                    canData = new String[lengthOfCanDataByCAN]; //0 0 69 0 46 2F BB BB
                                    System.arraycopy(strings, 2, canData, 0, lengthOfCanDataByCAN - 1 + 1);
                                } else {
                                    canData = strings;
                                }

                                return new DataHolder(strings[0], lengthOfCanDataByCAN, canData, 1);
                            } catch (Exception e) {
                                throw new RuntimeException();
                            }
                        }
                        ,
                        (dataHolder1, dataHolder2) -> {
                            final String[] canData1 = dataHolder1.getCanData();
                            final String[] canData2 = dataHolder2.getCanData();
                            for (int i = 0; i < canData2.length; i++) {
                                if (!canData1[i].equals(canData2[i])) {
                                    canData1[i] = "CHD";
                                }
                            }
                            dataHolder1.addCounter();
                            return dataHolder1;
                        }


                ));

        dataHolders.clear();
        dataHolders.addAll(stringDataHolderMap.values());
        canBusDataTableView.getItems().setAll(FXCollections.observableArrayList(dataHolders));
    }

}
