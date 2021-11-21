package calculator;

public class Data {


    private Integer value;

    private static final Data data = new Data();

    private Data() {
    }

    public Integer getValue() {
        return value;
    }


    public static Data getInstance(Integer value) {
        data.value = value;
        return data;
    }

}
