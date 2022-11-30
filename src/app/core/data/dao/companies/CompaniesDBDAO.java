package app.core.data.dao.companies;

import app.core.beans.Company;
import app.core.data.ConnectionPool;
import app.core.exceptions.CouponSystemException;
import app.core.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;

public class CompaniesDBDAO implements CompaniesDAO {


    ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean isCompanyExist(String email, String password) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM companies");

            while (resultSet.next()) {

                for (Company company : getAllCompanies()) {

                    if (company.getEmail().equals(resultSet.getString("email")) &&
                            company.getPassword().equals(resultSet.getString("password"))) {
                        return true;

                    }
                }
            }
            return false;

        } catch (SQLException | RuntimeException e) {
            throw new DAOException("Can't find company: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void addCompany(Company company) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO companies VALUES(?,?,?,?)");

            preparedStatement.setInt(1, company.getId());
            preparedStatement.setString(2, company.getName());
            preparedStatement.setString(3, company.getEmail());
            preparedStatement.setString(4, company.getPassword());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't add company: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void updateCompany(Company company) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE companies " +
                    "SET name = ?, email = ?, password = ? WHERE id = ?");

            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getEmail());
            preparedStatement.setString(3, company.getPassword());
            preparedStatement.setInt(4, company.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't update company: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    @Override
    public void deleteCompany(int companyID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM companies WHERE id = ?");

            statement.setInt(1, companyID);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't delete company " + companyID + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    @Override
    public ArrayList<Company> getAllCompanies() throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            ArrayList<Company> companies = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM companies");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                Company company = new Company(id, name, email, password);
                companies.add(company);
            }

            return companies;

        } catch (SQLException e) {
            throw new DAOException("Can't get all companies: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public Company getOneCompany(int companyID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM companies WHERE id = ?");
            statement.setInt(1, companyID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                return new Company(id, name, email, password);

            } else {
                throw new DAOException("Can't get company from id " + companyID);
            }

        } catch (SQLException e) {
            throw new DAOException("Can't get company: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }
}
