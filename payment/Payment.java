package payment;

import java.sql.Timestamp;

public class Payment {

    private final int paymentId;
    private final int bookingId;
    private final double amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String cardLast4;
    private final Timestamp transactionTime;

    public Payment(int paymentId,
            int bookingId,
            double amount,
            PaymentMethod method,
            PaymentStatus status,
            String cardLast4,
            Timestamp transactionTime) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.cardLast4 = cardLast4;
        this.transactionTime = transactionTime;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }
}
