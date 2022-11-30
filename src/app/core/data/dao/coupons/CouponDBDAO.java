package app.core.data.dao.coupons;

import app.core.beans.Coupon;
import app.core.beans.CouponPurchase;
import app.core.data.Category;
import app.core.data.ConnectionPool;
import app.core.exceptions.CouponSystemException;
import app.core.exceptions.DAOException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CouponDBDAO implements CouponsDAO {

    ConnectionPool connectionPool;

    public CouponDBDAO() {
        connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public void addCoupon(Coupon coupon) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO coupons " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?)");

            statement.setInt(1, coupon.getId());
            statement.setInt(2, coupon.getCompanyID());
            statement.setString(3, coupon.getCategory().name());
            statement.setString(4, coupon.getTitle());
            statement.setString(5, coupon.getDescription());
            statement.setDate(6, java.sql.Date.valueOf(coupon.getStartDate()));
            statement.setDate(7, java.sql.Date.valueOf(coupon.getEndDate()));
            statement.setInt(8, coupon.getAmount());
            statement.setDouble(9, coupon.getPrice());
            statement.setString(10, coupon.getImage());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't add coupon", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    @Override
    public void updateCoupon(Coupon coupon) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE coupons SET " +
                    "company_id = ?, category = ?, title = ?, description = ?, start_date = ?, end_date = ?, " +
                    "amount = ?, price = ?, image = ? WHERE id = ?");

            statement.setInt(1, coupon.getCompanyID());
            statement.setString(2, coupon.getCategory().name());
            statement.setString(3, coupon.getTitle());
            statement.setString(4, coupon.getDescription());
            statement.setDate(5, java.sql.Date.valueOf(coupon.getStartDate().toString()));
            statement.setDate(6, java.sql.Date.valueOf(coupon.getEndDate().toString()));
            statement.setInt(7, coupon.getAmount());
            statement.setDouble(8, coupon.getPrice());
            statement.setString(9, coupon.getImage());
            statement.setInt(10, coupon.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't update coupon " + coupon.getId() + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);

        }
    }

    @Override
    public void deleteCoupon(int couponID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM coupons WHERE id = ?");
            statement.setInt(1, couponID);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't delete coupon " + couponID + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public ArrayList<Coupon> getAllCoupons() throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            ArrayList<Coupon> coupons = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM coupons");

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int companyID = resultSet.getInt("company_id");
                Category category = Category.valueOf(resultSet.getString("category"));
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate start_date = resultSet.getDate("start_date").toLocalDate();
                LocalDate end_date = resultSet.getDate("end_date").toLocalDate();
                int amount = resultSet.getInt("amount");
                double price = resultSet.getDouble("price");
                String image = resultSet.getString("image");

                Coupon coupon = new Coupon(id, companyID, category, title, description, start_date, end_date, amount,
                        price, image);
                coupons.add(coupon);
            }

            return coupons;

        } catch (SQLException e) {
            throw new RuntimeException("Can't get all coupons: ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    @Override
    public Coupon getOneCoupon(int couponID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM coupons WHERE id = ?");

            statement.setInt(1, couponID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int companyID = resultSet.getInt("company_id");
                Category category = Category.valueOf(resultSet.getString("category"));
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate start_date = resultSet.getDate("start_date").toLocalDate();
                LocalDate end_date = resultSet.getDate("end_date").toLocalDate();
                int amount = resultSet.getInt("amount");
                double price = resultSet.getDouble("price");
                String image = resultSet.getString("image");

                return new Coupon(couponID, companyID, category, title, description, start_date, end_date, amount,
                        price, image);

            } else {
                throw new DAOException("Can't find coupon " + couponID + " in the database");
            }

        } catch (SQLException e) {
            throw new DAOException("Can't get coupon " + couponID + ": ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public ArrayList<Coupon> getCustomerCoupons(int customerID) throws DAOException {
        ArrayList<Coupon> coupons = new ArrayList<>();

        for (CouponPurchase couponPurchase : getCouponPurchasesByCustomer(customerID)) {
            coupons.add(getOneCoupon(couponPurchase.getCouponID()));
        }

        return coupons;
    }

    @Override
    public void addCouponPurchase(int customerID, int couponID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO customers_vs_coupons VALUES(?,?)");

            statement.setInt(1, customerID);
            statement.setInt(2, couponID);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't add a coupon purchase", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void addCouponPurchase(CouponPurchase couponPurchase) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO customers_vs_coupons VALUES(?,?)");

            statement.setInt(1, couponPurchase.getCustomerID());
            statement.setInt(2, couponPurchase.getCouponID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't add a coupon purchase", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void deleteCouponPurchase(int customerID, int couponID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM customers_vs_coupons WHERE customer_id = ? AND coupon_id = ?");

            statement.setInt(1, customerID);
            statement.setInt(2, couponID);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't remove coupon purchase ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public void deleteCouponPurchase(int couponID) throws DAOException {

        Connection connection;
        try {
            connection = connectionPool.getConnection();

        } catch (CouponSystemException e) {
            throw new DAOException("Can't get a connection ", e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM customers_vs_coupons WHERE coupon_id = ?");

            statement.setInt(1, couponID);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Can't remove coupon purchase ", e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    @Override
    public ArrayList<CouponPurchase> getCouponPurchases() throws DAOException {
        Connection connection;
        ArrayList<CouponPurchase> couponPurchases = new ArrayList<>();

        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customers_vs_coupons");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CouponPurchase couponPurchase = new CouponPurchase(resultSet.getInt("coupon_id"),
                        resultSet.getInt("customer_id"));
                couponPurchases.add(couponPurchase);
            }

            return couponPurchases;
        } catch (SQLException e) {
            throw new DAOException(e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public ArrayList<CouponPurchase> getCouponPurchasesByCustomer(int customerID) throws DAOException {
        Connection connection;
        ArrayList<CouponPurchase> couponPurchases = new ArrayList<>();

        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customers_vs_coupons " +
                    "WHERE customer_id = ?");
            preparedStatement.setInt(1, customerID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CouponPurchase couponPurchase = new CouponPurchase(resultSet.getInt("coupon_id"),
                        resultSet.getInt("customer_id"));
                couponPurchases.add(couponPurchase);
            }

            return couponPurchases;
        } catch (SQLException e) {
            throw new DAOException(e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }

    public ArrayList<CouponPurchase> getCouponPurchasesByCoupon(int couponID) throws DAOException {
        Connection connection;
        ArrayList<CouponPurchase> couponPurchases = new ArrayList<>();

        try {
            connection = connectionPool.getConnection();
        } catch (CouponSystemException e) {
            throw new DAOException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customers_vs_coupons " +
                    "WHERE coupon_id = ?");
            preparedStatement.setInt(1, couponID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CouponPurchase couponPurchase = new CouponPurchase(resultSet.getInt("coupon_id"),
                        resultSet.getInt("customer_id"));
                couponPurchases.add(couponPurchase);
            }

            return couponPurchases;
        } catch (SQLException e) {
            throw new DAOException(e);

        } finally {
            connectionPool.restoreConnection(connection);
        }
    }
}
