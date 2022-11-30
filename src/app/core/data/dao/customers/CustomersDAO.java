package app.core.data.dao.customers;

import app.core.beans.Customer;
import app.core.exceptions.DAOException;

import java.util.ArrayList;

public interface CustomersDAO {

    /**
     * @param email    Email of the customer
     * @param password Password of the customer
     * @return True if the customer exists in the database
     */
    boolean isCustomerExists(String email, String password) throws DAOException;

    /**
     * @param customer Customer to add to the database
     */
    void addCustomer(Customer customer) throws DAOException;

    /**
     * Update an existing customer in the database
     *
     * @param customer Customer to update
     */
    void updateCustomer(Customer customer) throws DAOException;

    /**
     * Delete a customer from the database
     *
     * @param customerID The ID of the customer to delete
     */
    void deleteCustomer(int customerID) throws DAOException;

    /**
     * @return A list of all customers in the database
     */
    ArrayList<Customer> getAllCustomers() throws DAOException;

    /**
     * @param customerID The ID of the customer to get
     * @return Object of the customer
     */
    Customer getOneCustomer(int customerID) throws DAOException;

}
