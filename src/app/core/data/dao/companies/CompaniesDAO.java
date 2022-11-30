package app.core.data.dao.companies;

import app.core.beans.Company;
import app.core.exceptions.DAOException;

import java.util.ArrayList;

public interface CompaniesDAO {

    /**
     * @param email    The company's email
     * @param password The company's password
     * @return True if the company with the email and password exists on the database
     */
    boolean isCompanyExist(String email, String password) throws DAOException;

    /**
     * Adds a new company to the database
     *
     * @param company Company to add to the database
     */
    void addCompany(Company company) throws DAOException;

    /**
     * Update an existing company from the database
     *
     * @param company The company to update
     */
    void updateCompany(Company company) throws DAOException;

    /**
     * Delete a company from the database
     *
     * @param companyID The ID of the company to delete
     */
    void deleteCompany(int companyID) throws DAOException;

    /**
     * @return All the companies in the database
     */
    ArrayList<Company> getAllCompanies() throws DAOException;

    /**
     * @param companyID The ID of the company to get
     * @return The object of the company from the database
     */
    Company getOneCompany(int companyID) throws DAOException;

}
