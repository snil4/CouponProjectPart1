package app.core.tests;

import app.core.beans.Company;
import app.core.beans.Coupon;
import app.core.beans.Customer;
import app.core.clients.AdminFacade;
import app.core.clients.ClientFacade;
import app.core.clients.CompanyFacade;
import app.core.clients.CustomerFacade;
import app.core.data.Category;
import app.core.data.ConnectionPool;
import app.core.exceptions.CouponSystemException;
import app.core.exceptions.FacadeException;
import app.core.exceptions.LoginException;
import app.core.tasks.ConnectionTest;
import app.core.tasks.CouponExpirationDailyJob;
import app.core.tasks.LoginManager;

import java.time.LocalDate;
import java.util.Date;

public class Test {

    LoginManager loginManager = LoginManager.getInstance();
    CouponExpirationDailyJob job = CouponExpirationDailyJob.getInstance();
    ConnectionPool connectionPool = ConnectionPool.getInstance();


    public synchronized void testAll() {

        try {
            // Test 1
            System.out.println("-----------Test 1----------");
            System.out.println("Date test (Job test will be initialized at the end for easier testing)");

            System.out.println(new Date());
            System.out.println();
            // Test 2
            System.out.println("-----------Test 2----------");
            System.out.println("-------------Admin test-----");

            ClientFacade clientFacade = loginManager.Login("admin@admin.com", "admin",
                    LoginManager.ClientType.Administrator);
            AdminFacade adminFacade = (AdminFacade) clientFacade;

            adminFacade.addCompany(new Company(19, "Shtraus", "shtraus@company.com", "moomoo"));
            adminFacade.addCompany(new Company(22, "Tnuva", "tnuva@company.com", "meemee"));
            adminFacade.addCompany(new Company(51, "Telma", "telma@company.com", "yammy"));

            adminFacade.addCustomer(new Customer(25, "Shelly", "Tal", "shelltal@gmail.com", "st12345"));
            adminFacade.addCustomer(new Customer(32, "Danny", "Nitay", "dannyn@gmail.com", "dn12345"));
            adminFacade.addCustomer(new Customer(48, "Yoni", "Katz", "ykatz@gmail.com", "yk12345"));

            Company tnuva = adminFacade.getOneCompany(22);
            System.out.println(tnuva);
            tnuva.setPassword("wow");
            adminFacade.updateCompany(tnuva);
            adminFacade.deleteCompany(51);
            System.out.println(adminFacade.getAllCompanies());

            Customer danny = adminFacade.getOneCustomer(32);
            System.out.println(danny);
            danny.setEmail("dannynit@gmail.com");
            adminFacade.updateCustomer(danny);
            adminFacade.deleteCustomer(48);
            System.out.println(adminFacade.getAllCustomers());
            System.out.println("(Coupon added manually from the SQL script so\nthe end date should be expired for " +
                    "test purposes)");
            System.out.println(adminFacade.getAllCoupons());

            System.out.println();
            // Test 3
            System.out.println("-----------Test 3----------");
            System.out.println("-----------Company Test----");

            clientFacade = loginManager.Login("shtraus@company.com", "moomoo", LoginManager.ClientType.Company);
            CompanyFacade companyFacade = (CompanyFacade) clientFacade;
            Company company = companyFacade.getCompanyDetails();
            System.out.println(company);

            Coupon coupon1 = new Coupon(1, company.getId(), Category.CLOTHING, "Cow T-shirt", "Get a free t-shirt",
                    LocalDate.of(2021, 3, 15), LocalDate.of(2023, 1, 15), 100, 50, "image.jpg");
            Coupon coupon2 = new Coupon(2, company.getId(), Category.ELECTRICITY, "Cow Toothbrush", "Get a free " +
                    "toothbrush!", LocalDate.of(2021, 3, 15), LocalDate.of(2022, 12, 15), 100, 10, "image.jpg");
            Coupon coupon3 = new Coupon(3, company.getId(), Category.ELECTRICITY, "Cow Paper", "Get a free paper!",
                    LocalDate.of(2021, 3, 15), LocalDate.of(2022, 12, 15), 100, 100, "image.jpg");

            companyFacade.addCoupon(coupon1);
            companyFacade.addCoupon(coupon2);
            companyFacade.addCoupon(coupon3);

            Coupon paper = companyFacade.getCompanyCoupons().get(2);
            paper.setPrice(110);
            companyFacade.updateCoupon(paper);

            System.out.println(companyFacade.getCompanyCoupons(Category.ELECTRICITY));
            companyFacade.deleteCoupon(2);
            System.out.println(companyFacade.getCompanyCoupons(60));

            System.out.println();
            // Test 4
            System.out.println("-----------Test 4----------");
            System.out.println("-----------Customer test---");

            companyFacade.addCoupon(coupon2);
            clientFacade = loginManager.Login(danny.getEmail(), danny.getPassword(), LoginManager.ClientType.Customer);
            CustomerFacade customerFacade = (CustomerFacade) clientFacade;
            Customer customer = customerFacade.getCustomerDetails();
            System.out.println(customer);

            customerFacade.purchaseCoupon(coupon1);
            customerFacade.purchaseCoupon(coupon2);
            customerFacade.purchaseCoupon(paper);

            System.out.println(customerFacade.getCustomerCoupons());
            System.out.println(customerFacade.getCustomerCoupons(Category.ELECTRICITY));
            System.out.println(customerFacade.getCustomerCoupons(100));

            System.out.println();
            // Test 5
            System.out.println("-----------Test 5----------");
            System.out.println("Start coupon expiration job");

            job.start();
            Thread.sleep(5000);
            job.stop();
            System.out.println("Job stopped");

            for (Coupon coupon : adminFacade.getAllCoupons()) {
                System.out.println(coupon);
            }

            System.out.println("Should not have 'cool headphones'.");

            System.out.println();

        } catch (FacadeException | LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        // Connection test
        System.out.println("--------Connections test--------");
        System.out.println("Take 10 connections from the pool, 5 should wait for a new connection to be free");
        ConnectionTestInitializer connectionTest = new ConnectionTestInitializer();

        connectionTest.start();
        try {
            connectionTest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test 6
        System.out.println("-----------Test 6----------");
        try {
            System.out.println("Close all connections");
            connectionPool.closeAllConnections();
            System.out.println("Connections closed");
        } catch (CouponSystemException e) {
            e.printStackTrace();
        }

    }
}
