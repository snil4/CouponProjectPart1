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
                for (Company company : COMPANIES_DAO.getAllCompanies()) {

                    if (company.getEmail().equals(email)) {
                        companyID = company.getId();

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
     * Add a new coupon to the company and the database
     *
     * @param coupon Coupon to add
     */
    public void addCoupon(Coupon coupon) throws FacadeException {
        try {
            Company company = COMPANIES_DAO.getOneCompany(companyID);
            ArrayList<Coupon> coupons = company.getCoupons();

            if (coupons != null) {
                for (Coupon check : coupons) {
                    if (check.getTitle().equals(coupon.getTitle())) {
                        throw new FacadeException("Can't add coupon, coupon with the same name already exists in " +
                                "company");
                    }
                }
            } else {
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
            ArrayList<Coupon> coupons = getCompanyCoupons();
            boolean found = false;

            for (Coupon check : coupons) {

                if (check.getId() == coupon.getId()) {
                    coupon.setCompanyID(check.getCompanyID());
                    coupons.remove(check);
                    coupons.add(coupon);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new FacadeException("No coupon was found for this company");
            }

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

            for (Coupon coupon : coupons) {
                if (coupon.getId() == couponID) {

                    coupons.remove(coupon);
                    COUPON_DAO.deleteCoupon(couponID);
                    break;
                }
            }

            COUPON_DAO.deleteCouponPurchase(couponID);

            company.setCoupons(coupons);
            COMPANIES_DAO.updateCompany(company);
        } catch (DAOException e) {
            throw new FacadeException("Can't delete coupon: ", e);
        }
    }

    /**
     * @return A list of coupons of this company in the database
     */
    public ArrayList<Coupon> getCompanyCoupons() throws FacadeException {
        try {
            ArrayList<Coupon> coupons = COUPON_DAO.getAllCoupons();
            ArrayList<Coupon> new_coupons = new ArrayList<>();

            for (Coupon coupon : coupons) {
                if (coupon.getCompanyID() == companyID) {
                    new_coupons.add(coupon);
                }
            }

            return new_coupons;
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
            ArrayList<Coupon> coupons = COUPON_DAO.getAllCoupons();
            ArrayList<Coupon> new_coupons = new ArrayList<>();

            for (Coupon coupon : coupons) {
                if (coupon.getCompanyID() == companyID && coupon.getCategory() == category) {
                    new_coupons.add(coupon);
                }
            }

            return new_coupons;
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
            ArrayList<Coupon> coupons = COUPON_DAO.getAllCoupons();
            ArrayList<Coupon> new_coupons = new ArrayList<>();

            for (Coupon coupon : coupons) {
                if (coupon.getCompanyID() == companyID & coupon.getPrice() < maxPrice) {
                    new_coupons.add(coupon);
                }
            }

            return new_coupons;
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
