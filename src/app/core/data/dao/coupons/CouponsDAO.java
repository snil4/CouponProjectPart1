package app.core.data.dao.coupons;

import app.core.beans.Coupon;
import app.core.beans.CouponPurchase;
import app.core.exceptions.DAOException;

import java.util.ArrayList;

public interface CouponsDAO {

    /**
     * Add a new coupon to the database
     *
     * @param coupon Coupon to add to the database
     */
    void addCoupon(Coupon coupon) throws DAOException;

    /**
     * Update an existing coupon from the database
     *
     * @param coupon Coupon to update
     */
    void updateCoupon(Coupon coupon) throws DAOException;

    /**
     * Delete a coupon from the database
     *
     * @param couponID The ID of the coupon to delete
     */
    void deleteCoupon(int couponID) throws DAOException;

    /**
     * @return A list of all coupons in the database
     */
    ArrayList<Coupon> getAllCoupons() throws DAOException;

    /**
     * Get a coupon from the database
     *
     * @param couponID The ID of the coupon to get
     * @return The object of the coupon
     */
    Coupon getOneCoupon(int couponID) throws DAOException;

    /**
     * Get a list of coupons owned by a specific customer
     *
     * @param customerID The ID of the customer to get the coupons of
     * @return A list of the customer coupons
     */
    ArrayList<Coupon> getCustomerCoupons(int customerID) throws DAOException;

    /**
     * Add a new coupon purchase to the database
     *
     * @param customerID The ID of the purchasing customer
     * @param couponID   The ID of the purchased coupon
     */
    void addCouponPurchase(int customerID, int couponID) throws DAOException;

    /**
     * Add a new coupon purchase to the database
     *
     * @param couponPurchase The coupon purchase to add
     */
    void addCouponPurchase(CouponPurchase couponPurchase) throws DAOException;

    /**
     * @return All coupon purchases in the database
     */
    ArrayList<CouponPurchase> getCouponPurchases() throws DAOException;

    /**
     * Get a list of coupon purchases by customer from the database
     *
     * @param customerID The ID of the customer
     * @return A list of coupon purchases
     */
    ArrayList<CouponPurchase> getCouponPurchasesByCustomer(int customerID) throws DAOException;

    /**
     * Get a list of coupon purchases by coupon from the database
     *
     * @param couponID The ID of the coupon
     * @return A list of coupon purchases
     */
    ArrayList<CouponPurchase> getCouponPurchasesByCoupon(int couponID) throws DAOException;

    /**
     * Delete a coupon purchase from the database
     *
     * @param customerID The ID of the purchasing customer
     * @param couponID   The ID of the purchased coupon
     */
    void deleteCouponPurchase(int customerID, int couponID) throws DAOException;

    /**
     * Delete all coupon purchases of a specific coupon
     *
     * @param couponID The ID of the coupon
     */
    void deleteCouponPurchase(int couponID) throws DAOException;
}
