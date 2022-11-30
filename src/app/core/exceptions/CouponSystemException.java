package app.core.exceptions;

public class CouponSystemException extends Exception {
    public CouponSystemException() {
        super();
    }

    public CouponSystemException(String message) {
        super(message);
    }

    public CouponSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponSystemException(Throwable cause) {
        super(cause);
    }

    protected CouponSystemException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
