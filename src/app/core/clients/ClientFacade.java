package app.core.clients;

import app.core.data.dao.companies.CompaniesDAO;
import app.core.data.dao.coupons.CouponsDAO;
import app.core.data.dao.customers.CustomersDAO;
import app.core.exceptions.FacadeException;

public abstract class ClientFacade {

    protected CompaniesDAO companiesDAO;
    protected CustomersDAO customersDAO;
    protected CouponsDAO couponsDAO;

    /**
     * @param email    The email of the client
     * @param password The password of the client
     * @return True if the login went successfully
     * @throws FacadeException In case of a connection error
     */
    public boolean login(String email, String password) throws FacadeException {
        return false;
    }
}
