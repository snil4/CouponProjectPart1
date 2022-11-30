package app.core.tasks;

import app.core.beans.Coupon;
import app.core.data.dao.coupons.CouponDBDAO;
import app.core.data.dao.coupons.CouponsDAO;
import app.core.exceptions.DAOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CouponExpirationDailyJob implements Runnable {

    private static CouponExpirationDailyJob instance = null;
    private final Thread THREAD = new Thread(this, "CouponExpirationJob");
    private final long WAIT_TIME = TimeUnit.DAYS.toMillis(1);
    private final CouponsDAO COUPONS_DAO = new CouponDBDAO();
    private boolean quit = false;
    private CouponExpirationDailyJob() {
    }

    public static CouponExpirationDailyJob getInstance() {

        if (instance == null) {
            instance = new CouponExpirationDailyJob();
        }

        return instance;
    }

    @Override
    public void run() {
        while (!quit) {
            try {
                ArrayList<Coupon> coupons = COUPONS_DAO.getAllCoupons();

                for (Coupon coupon : coupons) {
                    if (coupon.getEndDate().isBefore(LocalDate.now())) {
                        COUPONS_DAO.deleteCoupon(coupon.getId());
                        COUPONS_DAO.deleteCouponPurchase(coupon.getId());
                    }
                }

                synchronized (this) {
                    wait(WAIT_TIME);
                }

            } catch (InterruptedException e) {
                quit = true;

            } catch (DAOException e) {
                e.printStackTrace();
            }

        }
    }

    public void start() {
        THREAD.setDaemon(true);
        THREAD.start();
    }

    public void stop() {
        quit = true;
        this.THREAD.interrupt();
    }
}
