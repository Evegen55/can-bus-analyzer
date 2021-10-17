package data;

import java.util.Arrays;

public class DataHolder {

    private String deviceId;
    private int lengthOfCanData;
    private String[] canData;

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
        SimpleDict simpleDict = null;
        try {
            simpleDict = SimpleDict.valueOf("CAN_ID_" + deviceId);
        } catch (Exception e) {
            System.out.println("unknown " + "CAN_ID_ " + deviceId);
        }
        return simpleDict == null ? "" : simpleDict.getCanBusDeviceName();
    }

    public int getFrequencyCounter() {
        return frequencyCounter;
    }

    public void addCounter() {
        frequencyCounter += 1;
    }
}
