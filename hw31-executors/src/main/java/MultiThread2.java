import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThread2 {
    private final int COUNT_LIMIT = 10;
    private final Object monitor = new Object();
    private static final Logger logger = LoggerFactory.getLogger(MultiThread.class);

    private int count = 1;
    private boolean downFlag;

    private void task1() {
        while (count != 0) {
            synchronized (monitor) {
                logger.info(String.valueOf(count));
                monitor.notifyAll();
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void task2() {
        while (count != 0) {
            synchronized (monitor) {
                logger.info(String.valueOf(count));
                doUpAndDown();
                monitor.notifyAll();
                try {
                    if (count != 0) {
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doUpAndDown() {
        if (count == COUNT_LIMIT) {
            downFlag = true;
        }
        if (downFlag) {
            count--;
        } else {
            count++;
        }
    }

    public static void main(String[] args) {
        MultiThread2 multiThread = new MultiThread2();
        new Thread(multiThread::task1).start();
        new Thread(multiThread::task2).start();
    }
}
