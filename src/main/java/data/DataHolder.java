package data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static data.DeviceInfoStorage.DEVICE_INFO;

public class DataHolder {

    private String deviceId;
    private int lengthOfCanData;
    private String[] canData;
    private Set<String> uniqueCanMessages;

    private int frequencyCounter;

    public DataHolder(
            final String deviceId,
            final int lengthOfCanData,
            final String[] canData,
            final int frequencyCounter
    ) {
        this.deviceId = deviceId;
        this.lengthOfCanData = lengthOfCanData;
        this.canData = canData;
        this.frequencyCounter = frequencyCounter;
        HashSet<String> uniqueCanMessages = new HashSet<>();
        uniqueCanMessages.add(Arrays.toString(canData));
        this.uniqueCanMessages = uniqueCanMessages;
    }

    public int getLengthOfCanData() {
        return lengthOfCanData;
    }

    public String[] getCanData() {
        return canData;
    }

    public String getStringCanData() {
        return Arrays.toString(canData);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        DeviceInfoStorage.DeviceStoredInfo deviceStoredInfo = DEVICE_INFO.get(deviceId);
        return deviceStoredInfo == null ? "" : deviceStoredInfo.getDeviceName();
    }

    public String getDeviceCanArea() {
        DeviceInfoStorage.DeviceStoredInfo deviceStoredInfo = DEVICE_INFO.get(deviceId);
        return deviceStoredInfo == null ? "" : deviceStoredInfo.getCanArea();
    }

    public int getFrequencyCounter() {
        return frequencyCounter;
    }

    public void addCounter() {
        frequencyCounter += 1;
    }

    public Set<String> getUniqueCanMessages() {
        return uniqueCanMessages;
    }
}
