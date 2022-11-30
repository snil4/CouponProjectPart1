package app.core.beans;

// Class for better management of coupon purchases
public class CouponPurchase {

    private int couponID;
    private int customerID;

    public CouponPurchase() {
    }

    public CouponPurchase(int couponID, int customerID) {
        this.couponID = couponID;
        this.customerID = customerID;
    }

    public int getCouponID() {
        return couponID;
    }

    public void setCouponID(int couponID) {
        this.couponID = couponID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
}
