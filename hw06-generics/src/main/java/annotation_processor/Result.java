package annotation_processor;

public class Result {

    private boolean status;
    private String testDescribe;
    private String className;
    private String exception;
    private String methodName;


    public Result(String methodName, String testDescribe, String className) {
        this.testDescribe = testDescribe;
        this.className = className;
        this.methodName = methodName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTestDescription() {
        return testDescribe;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

}
