package app.core.tasks;

import app.core.clients.AdminFacade;
import app.core.clients.ClientFacade;
import app.core.clients.CompanyFacade;
import app.core.clients.CustomerFacade;
import app.core.exceptions.FacadeException;
import app.core.exceptions.LoginException;

// Singleton class that manages logins and returns a user object base on input
public class LoginManager {

    private static LoginManager instance = null;
    private final AdminFacade ADMIN_FACADE = new AdminFacade();
    private final CompanyFacade COMPANY_FACADE = new CompanyFacade();
    private final CustomerFacade CUSTOMER_FACADE = new CustomerFacade();

    private LoginManager() {
    }

    public static LoginManager getInstance() {

        if (instance == null) {
            instance = new LoginManager();
        }

        return instance;
    }

    /**
     * @param email      Client's email
     * @param password   Client's password
     * @param clientType The client's type
     * @return Object of the client based on input
     */
    public ClientFacade Login(String email, String password, ClientType clientType) throws LoginException {

        try {
            if (clientType == ClientType.Administrator && ADMIN_FACADE.login(email, password)) {
                return ADMIN_FACADE;

            } else if (clientType == ClientType.Company && COMPANY_FACADE.login(email, password)) {
                return COMPANY_FACADE;

            } else if (clientType == ClientType.Customer && CUSTOMER_FACADE.login(email, password)) {
                return CUSTOMER_FACADE;

            }

        } catch (FacadeException e) {
            throw new LoginException("Can't login to client", e);
        }

        return null;
    }


    public enum ClientType {
        Administrator, Company, Customer
    }
}
