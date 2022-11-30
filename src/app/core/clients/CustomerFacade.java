package app.core.clients;

import app.core.beans.Coupon;
import app.core.beans.Customer;
import app.core.data.Category;
import app.core.data.dao.coupons.CouponDBDAO;
import app.core.data.dao.customers.CustomersDBDAO;
import app.core.exceptions.DAOException;
import app.core.exceptions.FacadeException;

import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerFacade extends ClientFacade {

    private final CustomersDBDAO CUSTOMERS_DAO = new CustomersDBDAO();
    private final CouponDBDAO COUPON_DAO = new CouponDBDAO();
    private int customerID;

    public CustomerFacade() {
    }

    @Override
    public boolean login(String email, String password) throws FacadeException {

        try {
            if (CUSTOMERS_DAO.isCustomerExists(email, password)) {

                for (Customer customer : CUSTOMERS_DAO.getAllCustomers()) {

                    if (customer.getEmail().equals(email)) {
                        customerID = customer.getId();
                    }
                }

                return true;

            } else {
                return false;
            }

        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param coupon Coupon to add to the customer
     */
    public void purchaseCoupon(Coupon coupon) throws FacadeException {
        try {
            Customer customer = CUSTOMERS_DAO.getOneCustomer(customerID);
            ArrayList<Coupon> coupons = COUPON_DAO.getCustomerCoupons(customerID);

            if (coupon.getAmount() <= 0) {
                throw new FacadeException("This coupon's amount is 0 ");
            } else if (coupon.getEndDate().isBefore(LocalDate.now())) {
                throw new FacadeException("This coupon is expired");
            }

            for (Coupon check : coupons) {
                if (coupon.equals(check)) {
                    throw new FacadeException("Customer already has this coupon");
                }
            }

            coupon.setAmount(coupon.getAmount() - 1);
            coupons.add(coupon);
            COUPON_DAO.updateCoupon(coupon);
            customer.setCoupons(coupons);
            CUSTOMERS_DAO.updateCustomer(customer);
            COUPON_DAO.addCouponPurchase(customerID, coupon.getId());

        } catch (DAOException e) {
            throw new FacadeException("Can't purchase coupon: ", e);
        }

    }

    /**
     * @return A list of all the coupons the customer bought
     */
    public ArrayList<Coupon> getCustomerCoupons() throws FacadeException {
        try {
            return CUSTOMERS_DAO.getOneCustomer(customerID).getCoupons();
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param category The category to return the coupons of
     * @return A list of all the coupons the customer bought
     */
    public ArrayList<Coupon> getCustomerCoupons(Category category) throws FacadeException {
        ArrayList<Coupon> coupons = new ArrayList<>();

        try {
            for (Coupon coupon : CUSTOMERS_DAO.getOneCustomer(customerID).getCoupons()) {
                if (coupon.getCategory() == category) {
                    coupons.add(coupon);
                }
            }
        } catch (DAOException e) {
            throw new FacadeException(e);
        }

        return coupons;
    }

    /**
     * @param maxPrice The maximum price of the coupons to return
     * @return A list of all the coupons the customer bought
     */
    public ArrayList<Coupon> getCustomerCoupons(double maxPrice) throws FacadeException {
        ArrayList<Coupon> coupons = new ArrayList<>();

        try {
            for (Coupon coupon : COUPON_DAO.getCustomerCoupons(customerID)) {
                if (coupon.getPrice() <= maxPrice) {
                    coupons.add(coupon);
                }
            }
        } catch (DAOException e) {
            throw new FacadeException(e);
        }

        return coupons;
    }

    /**
     * @return The object of the customer
     */
    public Customer getCustomerDetails() throws FacadeException {
        try {
            Customer customer = CUSTOMERS_DAO.getOneCustomer(customerID);
            customer.setCoupons(getCustomerCoupons());
            return customer;
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }
}
