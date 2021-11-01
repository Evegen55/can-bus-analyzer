package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DeviceInfoStorage {

    public static final Map<String, DeviceStoredInfo> DEVICE_INFO = initMapFromFile();

    private static Map<String, DeviceStoredInfo> initMapFromFile() {
        try {
            return Files.lines(Path.of("src/main/resources/devices.csv"))
                    .skip(1)
                    .map(line -> line.split(","))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> new DeviceStoredInfo(arr[1], arr[2])));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class DeviceStoredInfo {
        private String deviceName;
        private String canArea;

        public DeviceStoredInfo(String deviceName, String canArea) {
            this.deviceName = deviceName;
            this.canArea = canArea;
        }

        public String getCanArea() {
            return canArea;
        }

        public String getDeviceName() {
            return deviceName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceStoredInfo that = (DeviceStoredInfo) o;
            return Objects.equals(canArea, that.canArea) && Objects.equals(deviceName, that.deviceName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(canArea, deviceName);
        }
    }

}
