package app;

import data.DataHolder;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileCanDataScanner {

    private List<DataHolder> dataHolders;
    private TableView<DataHolder> canBusDataTableView;

    public FileCanDataScanner(
            final List<DataHolder> dataHolders,
            final TableView<DataHolder> canBusDataTableView
    ) {
        this.dataHolders = dataHolders;
        this.canBusDataTableView = canBusDataTableView;
    }

    public void openFileAndFillTable(File file) throws IOException {
        //19:08:46.657 -> 568 8 0 0 69 0 46 2F BB BB
        final Map<String, DataHolder> stringDataHolderMap = Files.lines(file.toPath())
                .skip(3) //lines with log descriptions
                .map(line -> {
                         String fileName = file.getName();
                         String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
                         String lineCanDataOnly;
                         if (fileExtension.equals("lgt")) {
                             lineCanDataOnly = line.split(" -> ")[1];//removing time label put by arduino logger
                         } else if (fileExtension.equals("lnt")) {
                             lineCanDataOnly = line;
                         } else {
                             throw new RuntimeException("File must be formated as " +
                                                        ".lgt - logged data with timings " +
                                                        "or " +
                                                        ".lnt - logged data without timings");
                         }
                         return lineCanDataOnly.split(" ");
                     }
                )
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
                                e.printStackTrace();
                                System.out.println(Arrays.toString(strings));
                                throw new RuntimeException();
                            }
                        }
                        ,
                        (dataHolder1, dataHolder2) -> {
                            final String[] canData1 = dataHolder1.getCanData();
                            final String[] canData2 = dataHolder2.getCanData();
                            if (canData1.length == canData2.length) {
                                for (int i = 0; i < canData2.length; i++) {
                                    if (!canData1[i].equals(canData2[i])) {
                                        canData1[i] = "*";
                                    }
                                }
                            }
                            dataHolder1.addCounter();
                            dataHolder1.getUniqueCanMessages().add(dataHolder2.getStringCanData());
                            return dataHolder1;
                        }
                ));

        dataHolders.clear();
        dataHolders.addAll(stringDataHolderMap.values());
        canBusDataTableView.getItems().setAll(FXCollections.observableArrayList(dataHolders));
    }
}
