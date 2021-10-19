package data;

@Deprecated
public enum SimpleDict {

    CAN_ID_5B8 ("radiosat_1"),
    CAN_ID_3DF ("radiosat_1");

    private final String canBusDeviceName;

    SimpleDict(final String canBusDeviceName) {
        this.canBusDeviceName = canBusDeviceName;
    }

    public String getCanBusDeviceName() {
        return canBusDeviceName;
    }
}
