package data;

public enum SimpleDict {

    CAN_ID_5B8 ("CARMINAT_1"),
    CAN_ID_3DF ("CARMINAT_2");

    private final String canBusDeviceName;

    SimpleDict(final String canBusDeviceName) {
        this.canBusDeviceName = canBusDeviceName;
    }

    public String getCanBusDeviceName() {
        return canBusDeviceName;
    }
}
