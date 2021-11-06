package app;

import com.fazecast.jSerialComm.SerialPort;
import data.DataHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComPortCanScanner {

    private Button startStopScanCANBusViaComPortButton;
    private ComboBox<String> existedComPortsComboBox;
    private SerialPort[] commPorts;
    private TableView<DataHolder> canBusDataTableView;

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    public ComPortCanScanner(
            final Button startStopScanCANBusViaComPortButton,
            final ComboBox<String> existedComPortsComboBox,
            final SerialPort[] commPorts,
            final TableView<DataHolder> canBusDataTableView
    ) {
        this.startStopScanCANBusViaComPortButton = startStopScanCANBusViaComPortButton;
        this.existedComPortsComboBox = existedComPortsComboBox;
        this.commPorts = commPorts;
        this.canBusDataTableView = canBusDataTableView;
    }

    ThreadSafeBoolFlag isComPortScanned = new ThreadSafeBoolFlag();
    //first thread reads from com and write to a buffer
    //second thread reads from buffer, form can-messages and write into can-messages buffer
    //third thread reads messages from buffer, validate them and update GUI with new data
    final ConcurrentLinkedDeque<Integer> charBuffer = new ConcurrentLinkedDeque<>();
    final ConcurrentLinkedDeque<String> canMessagesBuffer = new ConcurrentLinkedDeque<>();
    SerialPort comPort;
    ThreadSafeBoolFlag isComPortOpened = new ThreadSafeBoolFlag();

    final Runnable runnableReaderFromComPort = () -> {
        final int selectedIndexOfComPort = existedComPortsComboBox
                .getSelectionModel()
                .getSelectedIndex();
        if (selectedIndexOfComPort >= 0) {
            comPort = commPorts[selectedIndexOfComPort];
            isComPortOpened.set(comPort.openPort());
            if (isComPortOpened.get()) {
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
                try (InputStream in = comPort.getInputStream()) {
                    while (isComPortScanned.get()) {
                        int read = in.read();
                        if (read != -1) {
                            charBuffer.offer(read);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    final Runnable readerCanMessagesFromBuffer = () -> {
        StringBuilder sb = new StringBuilder();
        while (isComPortScanned.get()) {
            if (!charBuffer.isEmpty()) {
                Integer value = charBuffer.poll();
                char readFromBuffer = (char) value.intValue();
                if (readFromBuffer != '\r') {
                    if (readFromBuffer == '\n') {
                        canMessagesBuffer.offer(sb.toString()); //TODO print to log file line by line
                        sb = new StringBuilder();
                    } else {
                        sb.append(readFromBuffer);
                    }
                }
            }
        }
    };

    final Runnable updateUiFromComPort = () -> {
        Map<String, DataHolder> stringDataHolderMap = new HashMap<>();
        while (isComPortScanned.get()) {
            DataHolder dataHolder;
            if (!canMessagesBuffer.isEmpty()) {
                String canMessage = canMessagesBuffer.poll();
                String[] split = canMessage.split(" ");
                String deviceId = split[0];
                if (deviceId.length() >= 2) {
                    try {
                        final int lengthOfCanDataByCAN = Integer.parseInt(split[1]);
                        final int lengthOfCanDataActual = split.length - 2;
                        final String[] canData;
                        if (lengthOfCanDataByCAN == lengthOfCanDataActual) {
                            canData = new String[lengthOfCanDataByCAN]; //0 0 69 0 46 2F BB BB
                            System.arraycopy(split, 2, canData, 0, lengthOfCanDataByCAN - 1 + 1);
                            dataHolder = new DataHolder(deviceId, lengthOfCanDataByCAN, canData, 1);

                            if (!stringDataHolderMap.containsKey(deviceId)) {
                                stringDataHolderMap.put(deviceId, dataHolder);
                            } else {
                                DataHolder dataHolderPrev = stringDataHolderMap.get(deviceId);
                                String[] canDataPrev = dataHolderPrev.getCanData();
                                for (int i = 0; i < canData.length; i++) {
                                    if (!canDataPrev[i].equals(canData[i])) {
                                        canDataPrev[i] = "*";
                                    }
                                }
                                dataHolderPrev.addCounter();
                                dataHolderPrev.getUniqueCanMessages().add(dataHolder.getStringCanData());
                            }

                            ObservableList<DataHolder> dataHolderObservableList = FXCollections.observableArrayList(stringDataHolderMap.values());
                            canBusDataTableView.getItems().setAll(dataHolderObservableList);
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error on data from " + deviceId, e);
                    }
                } else {
                    System.out.println(deviceId);
                }
            }
        }
    };

    final ExecutorService executorServiceForComPort = Executors.newFixedThreadPool(3);
    Future<?> submitReaderFromComPort;
    Future<?> submitReaderCanMessagesFromBuffer;
    Future<?> submitUpdateUiFromComPort;

    public void startStopScanCANBusViaComPortButtonClicked() {
        if (isComPortScanned.get()) {
            //close port
            //stop job
            isComPortScanned.set(false);
            if (isComPortOpened.get()) {
                isComPortOpened.set(!comPort.closePort());
            }
            while (!submitReaderFromComPort.isCancelled() &&
                   !submitReaderCanMessagesFromBuffer.isCancelled() &&
                   !submitUpdateUiFromComPort.isCancelled()
            ) {
                submitReaderFromComPort.cancel(true);
                submitReaderCanMessagesFromBuffer.cancel(true);
                submitUpdateUiFromComPort.cancel(true);
            }
            startStopScanCANBusViaComPortButton.setText("Start scan CAN bus");
        } else {
            //open port
            //start scan
            isComPortScanned.set(true);
            canBusDataTableView.getItems().clear();
            while ((submitReaderFromComPort == null || submitReaderFromComPort.isDone()) &&
                   (submitReaderCanMessagesFromBuffer == null || submitReaderCanMessagesFromBuffer.isDone()) &&
                   (submitUpdateUiFromComPort == null || submitUpdateUiFromComPort.isDone())
            ) {
                submitReaderFromComPort = executorServiceForComPort.submit(runnableReaderFromComPort);
                submitReaderCanMessagesFromBuffer = executorServiceForComPort.submit(readerCanMessagesFromBuffer);
                submitUpdateUiFromComPort = executorServiceForComPort.submit(updateUiFromComPort);
                startStopScanCANBusViaComPortButton.setText("Stop scan CAN bus");
            }
        }
    }


    //https://stackoverflow.com/questions/48790650/how-to-swap-boolean-value-in-multi-threaded-environment
    public static class ThreadSafeBoolFlag {
        private boolean flag = false;

        public synchronized boolean get() {
            return flag;
        }

        public synchronized void set(boolean newValue) {
            flag = newValue;
        }

        public synchronized void swap() {
            flag = !flag;
        }

    }
}

