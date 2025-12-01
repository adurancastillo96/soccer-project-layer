package domain;

/**
 * A single main.java.domain exception used to signal all business rule violations or
 * main.java.persistence errors. An error code identifies the category of the problem
 * and a descriptive message provides further details for the user.
 */
public class DomainException extends RuntimeException {
    private final DomainErrorCode errorCode;

    /**
     * Constructs a new main.java.domain exception with the given error code and message.
     *
     * @param errorCode the code representing the category of error
     * @param message   a human readable description of the problem
     */
    public DomainException(DomainErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public DomainErrorCode getErrorCode() { return this.errorCode; }

    @Override
    public String toString() {
        return this.errorCode + ": " + super.getMessage();
    }

}
