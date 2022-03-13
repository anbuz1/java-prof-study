import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThread {
    private final int COUNT_LIMIT = 10;
    private static final Logger logger = LoggerFactory.getLogger(MultiThread.class);
    private boolean whoIsInCharge = true;
    private boolean downFlag;
    private boolean isStarted;
    private int count = 1;

    private synchronized void counting() {
        String name = Thread.currentThread().getName();
        if (name.equals("Thread-1") & !isStarted) {
            while (!isStarted) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (name.equals("Thread-0") & !isStarted) {
            isStarted = true;
            this.notifyAll();
        }
        try {
            while (count != 0) {
                this.notifyAll();
                logger.info(String.valueOf(count));

                if (whoIsInCharge) {
                    whoIsInCharge = false;

                    while (!whoIsInCharge) {
                        this.wait();
                    }
                } else {
                    whoIsInCharge = true;
                    doUpAndDown();
                    if (count != 0) {
                        while (whoIsInCharge) {
                            this.wait();
                        }
                    }
                }
            }
        } catch (InterruptedException ex) {
            logger.info(ex.toString());
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
        MultiThread multiThread = new MultiThread();
        Thread thread = new Thread(multiThread::counting);
        Thread thread1 = new Thread(multiThread::counting);
        thread.setName("Thread-0");
        thread1.setName("Thread-1");

        thread1.start();
        thread.start();
    }
}
