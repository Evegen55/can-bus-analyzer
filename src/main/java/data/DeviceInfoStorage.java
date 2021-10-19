package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class DeviceInfoStorage {

    public static final Map<String, String> DEVICE_INFO = initMapFromFile();

    private static Map<String, String> initMapFromFile() {
        try {
            return Files.lines(Path.of("src/main/resources/devices.csv"))
                    .skip(1)
                    .map(line -> line.split(","))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
