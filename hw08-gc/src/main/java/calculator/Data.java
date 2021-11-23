package calculator;

public class Data {


    private int value;

    private static final Data data = new Data();

    private Data() {
    }

    public int getValue() {
        return value;
    }


    public static Data getInstance(Integer value) {
        data.value = value;
        return data;
    }

}
