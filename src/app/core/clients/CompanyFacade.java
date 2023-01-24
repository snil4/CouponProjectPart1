package app.core.clients;

import app.core.beans.Company;
import app.core.beans.Coupon;
import app.core.data.Category;
import app.core.data.dao.companies.CompaniesDBDAO;
import app.core.data.dao.coupons.CouponDBDAO;
import app.core.exceptions.DAOException;
import app.core.exceptions.FacadeException;

import java.util.ArrayList;

public class CompanyFacade extends ClientFacade {

    private final CompaniesDBDAO COMPANIES_DAO = new CompaniesDBDAO();
    private final CouponDBDAO COUPON_DAO = new CouponDBDAO();
    private int companyID;

    public CompanyFacade() {

    }

    @Override
    public boolean login(String email, String password) throws FacadeException {
        try {
            if (COMPANIES_DAO.isCompanyExist(email, password)) {
                companyID = COMPANIES_DAO.getOneCompany(email).getId();
                return true;

            } else {
                return false;
            }

        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Add a new coupon to the company and the database
     *
     * @param coupon Coupon to add
     */
    public void addCoupon(Coupon coupon) throws FacadeException {
        try {

            if (COUPON_DAO.isCouponExists(coupon.getTitle())) {
                throw new FacadeException("Coupon with the same title already exists");
            }

            Company company = COMPANIES_DAO.getOneCompany(companyID);
            ArrayList<Coupon> coupons = company.getCoupons();

            if (coupons == null) {
                coupons = new ArrayList<>();
            }

            coupon.setCompanyID(companyID);
            coupons.add(coupon);
            company.setCoupons(coupons);
            COMPANIES_DAO.updateCompany(company);
            COUPON_DAO.addCoupon(coupon);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Update an existing coupon in the database
     *
     * @param coupon Coupon to update
     */
    public void updateCoupon(Coupon coupon) throws FacadeException {
        try {
            Company company = COMPANIES_DAO.getOneCompany(companyID);
            ArrayList<Coupon> coupons = COUPON_DAO.getAllCoupons();
            Coupon newCoupon = COUPON_DAO.getOneCoupon(coupon.getId());

            coupon.setCompanyID(newCoupon.getCompanyID());
            coupons.remove(newCoupon);
            coupons.add(coupon);

            company.setCoupons(coupons);
            COMPANIES_DAO.updateCompany(company);
            COUPON_DAO.updateCoupon(coupon);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Delete a coupon from the database based on ID
     *
     * @param couponID The ID of the coupon to delete
     */
    public void deleteCoupon(int couponID) throws FacadeException {

        try {
            Company company = COMPANIES_DAO.getOneCompany(companyID);
            ArrayList<Coupon> coupons = getCompanyCoupons();

            if (COUPON_DAO.isCouponExists(couponID)) {
                    coupons.remove(COUPON_DAO.getOneCoupon(couponID));
                    COUPON_DAO.deleteCoupon(couponID);
                    COUPON_DAO.deleteCouponPurchase(couponID);
                    company.setCoupons(coupons);
                    COMPANIES_DAO.updateCompany(company);
            } else {
                throw new FacadeException("Coupon with ID " + couponID + " doesn't exist in company ");
            }
        } catch (DAOException e) {
            throw new FacadeException("Can't delete coupon: ", e);
        }
    }

    /**
     * @return A list of coupons of this company in the database
     */
    public ArrayList<Coupon> getCompanyCoupons() throws FacadeException {
        try {
            return COUPON_DAO.getAllCompanyCoupons(companyID);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param category The category of the coupons
     * @return A list of coupons from this company filtered by category
     */
    public ArrayList<Coupon> getCompanyCoupons(Category category) throws FacadeException {
        try {
            return COUPON_DAO.getAllCompanyCoupons(companyID,category);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param maxPrice The maximum price of the coupons
     * @return A list of coupons from this company that do not exceed the maximum price
     */
    public ArrayList<Coupon> getCompanyCoupons(double maxPrice) throws FacadeException {
        try {
            return COUPON_DAO.getAllCompanyCoupons(companyID,maxPrice);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @return The object of the company
     */
    public Company getCompanyDetails() throws FacadeException {
        try {
            return COMPANIES_DAO.getOneCompany(companyID);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }
}
