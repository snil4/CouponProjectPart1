package app.core.data.dao.customers;

import app.core.beans.Customer;
import app.core.data.ConnectionPool;
import app.core.data.dao.coupons.CouponDBDAO;
import app.core.exceptions.CouponSystemException;
import app.core.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;

public class CustomersDBDAO implements CustomersDAO {

    ConnectionPool connectionPool = ConnectionPool.getInstance();
    CouponDBDAO couponDBDAO = new CouponDBDAO();

    @Override
    public boolean isCustomerExists(String email, String password) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()) {

                for (Customer customer : getAllCustomers()) {

                    if (customer.getEmail().equals(resultSet.getString("email")) &&
                            customer.getPassword().equals(resultSet.getString("password"))) {
                        return true;

                    }
                }
            }
            return false;

        } catch (SQLException e) {
            throw new DAOException("Can't find customer: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void addCustomer(Customer customer) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customers VALUES(?,?,?,?," +
                    "?)");

            preparedStatement.setInt(1, customer.getId());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setString(4, customer.getEmail());
            preparedStatement.setString(5, customer.getPassword());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't add customer: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE customers " +
                    "SET first_name = ?,last_name = ?, email = ?, password = ? WHERE id = ?");

            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setString(4, customer.getPassword());
            preparedStatement.setInt(5, customer.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't update customer " + customer.getId() + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }


    }

    @Override
    public void deleteCustomer(int customerID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE id = ?");

            statement.setInt(1, customerID);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't delete customer " + customerID + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    @Override
    public ArrayList<Customer> getAllCustomers() throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            ArrayList<Customer> customers = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                Customer customer = new Customer(id, first_name, last_name, email, password);
                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            throw new DAOException("Can't get all customers: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public Customer getOneCustomer(int customerID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
            statement.setInt(1, customerID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                Customer customer = new Customer(id, first_name, last_name, email, password);
                customer.setCoupons(couponDBDAO.getCustomerCoupons(customerID));

                return customer;

            } else {
                throw new DAOException("Can't get company from id " + customerID);
            }

        } catch (SQLException e) {
            throw new DAOException("Can't get company: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }
}
