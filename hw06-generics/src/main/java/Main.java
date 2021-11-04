import static annotation_processor.Processor.start;

public class Main {
    public static void main(String[] args) {

        start(System.getProperty("java.class.path").split(";")[0]);

    }
}
