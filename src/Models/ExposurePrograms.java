package Models;

public enum ExposurePrograms {
    NotDefined(0),
    Manual(1),
    Normal(2),
    AperturePriority(3),
    ShutterPriority(4),
    CreativeProgram(5),
    ActionProgram(6),
    PortraitMode(7),
    LandscapeMode(8);

    private int value;

    private ExposurePrograms(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
