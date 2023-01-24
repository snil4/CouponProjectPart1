package app.core.clients;

import app.core.beans.Company;
import app.core.beans.Coupon;
import app.core.beans.Customer;
import app.core.data.dao.companies.CompaniesDBDAO;
import app.core.data.dao.coupons.CouponDBDAO;
import app.core.data.dao.customers.CustomersDBDAO;
import app.core.exceptions.DAOException;
import app.core.exceptions.FacadeException;

import java.util.ArrayList;

public class AdminFacade extends ClientFacade {

    private final String EMAIL = "admin@admin.com";
    private final String PASSWORD = "admin";

    private final CompaniesDBDAO COMPANIES_DAO = new CompaniesDBDAO();
    private final CustomersDBDAO CUSTOMERS_DAO = new CustomersDBDAO();
    private final CouponDBDAO COUPON_DAO = new CouponDBDAO();

    @Override
    public boolean login(String email, String password) {
        return email.equals(this.EMAIL) && (password.equals(this.PASSWORD));
    }


    /**
     * Adds a new company to the database
     *
     * @param company The company to add to the database
     */
    public void addCompany(Company company) throws FacadeException {

        try {
            if (COMPANIES_DAO.isCompanyExistByEmail(company.getEmail()) || COMPANIES_DAO.isCompanyExistByName(company.getName())) {
                throw new FacadeException("A company with the same email or name already exists");
            }

            COMPANIES_DAO.addCompany(company);

        } catch (DAOException e) {
            throw new FacadeException(e);
        }

    }


    /**
     * Update an existing company in the database
     *
     * @param company The company to update in the database
     */
    public void updateCompany(Company company) throws FacadeException {

        try {
            Company check = COMPANIES_DAO.getOneCompany(company.getId());
            if (check != null) {
                company.setName(check.getName());
            } else {
                throw new FacadeException("Company with ID " + company.getId() + "Doesn't exist.");
            }
            COMPANIES_DAO.updateCompany(company);
        } catch (DAOException e) {
            throw new FacadeException();
        }

    }


    /**
     * Deletes a company from the database
     *
     * @param companyID The ID of the company to delete from the database
     */
    public void deleteCompany(int companyID) throws FacadeException {
        try {
            if (COMPANIES_DAO.getOneCompany(companyID) != null) {

                if (COMPANIES_DAO.getOneCompany(companyID).getCoupons() != null) {
                    COUPON_DAO.deleteCouponPurchaseByCompany(companyID);
                    COUPON_DAO.deleteCouponsByCompany(companyID);
                }

                COMPANIES_DAO.deleteCompany(companyID);

            } else {
                throw new FacadeException("Company with ID " + companyID + " doesn't exist");

            }

        } catch (DAOException e) {
            throw new FacadeException("Can't delete company: ", e);
        }

    }

    /**
     * @return A list of all the companies in the database
     */
    public ArrayList<Company> getAllCompanies() throws FacadeException {
        try {
            return COMPANIES_DAO.getAllCompanies();
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param companyID The company to get from the database
     * @return Company from the database with the ID
     */
    public Company getOneCompany(int companyID) throws FacadeException {
        try {
            return COMPANIES_DAO.getOneCompany(companyID);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Adds a customer to the database
     *
     * @param customer The customer to add to the database
     */
    public void addCustomer(Customer customer) throws FacadeException {
        try {

                if (CUSTOMERS_DAO.isCustomerExists(customer.getEmail())) {
                    throw new FacadeException("Customer with the same email already exists");
                }

            CUSTOMERS_DAO.addCustomer(customer);

        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Update an existing customer in the database
     *
     * @param customer The customer to update in the database
     */
    public void updateCustomer(Customer customer) throws FacadeException {
        try {
            CUSTOMERS_DAO.updateCustomer(customer);
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * Delete a customer from the database
     *
     * @param customerID The ID of the customer to delete
     */
    public void deleteCustomer(int customerID) throws FacadeException {
        try {
            Customer customer = CUSTOMERS_DAO.getOneCustomer(customerID);
            if (customer != null) {
                if (customer.getCoupons() != null) {
                        COUPON_DAO.deleteCouponPurchase(customerID);
                }

                CUSTOMERS_DAO.deleteCustomer(customerID);

            } else {
                throw new FacadeException("Customer with ID " + customerID + " doesn't exist");

            }
        } catch (DAOException e) {
            throw new FacadeException("Can't delete customer: ", e);

        }
    }

    /**
     * @return A list of all the customers in the database
     */
    public ArrayList<Customer> getAllCustomers() throws FacadeException {

        try {
            return CUSTOMERS_DAO.getAllCustomers();
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    /**
     * @param customerID The ID of the customer to get from the database
     * @return The customer from the database
     */
    public Customer getOneCustomer(int customerID) throws FacadeException {

        try {
            Customer customer = CUSTOMERS_DAO.getOneCustomer(customerID);
            customer.setCoupons(COUPON_DAO.getCustomerCoupons(customerID));

            return customer;
        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }

    public ArrayList<Coupon> getAllCoupons() throws FacadeException {

        try {
            return COUPON_DAO.getAllCoupons();

        } catch (DAOException e) {
            throw new FacadeException(e);
        }
    }
}
