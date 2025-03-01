package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    private int  errorCode = 0;
    private String errorMessage = "";
    public DataAccessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
    public String getBody() {
        return String.format("{\"message\":\"Error: %s\"}", this.errorMessage);
    }

}
