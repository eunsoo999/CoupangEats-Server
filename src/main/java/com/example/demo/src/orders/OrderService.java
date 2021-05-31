package com.example.demo.src.orders;

import com.example.demo.config.BaseException;
import com.example.demo.src.coupon.CouponDao;
import com.example.demo.src.menu.MenuDao;
import com.example.demo.src.orders.model.PostOrderMenus;
import com.example.demo.src.orders.model.PostOrderReq;
import com.example.demo.src.orders.model.PostOrderRes;
import com.example.demo.src.store.StoreDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class OrderService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private OrderDao orderDao;
    private StoreDao storeDao;
    private CouponDao couponDao;
    private MenuDao menuDao;

    @Autowired
    public OrderService(OrderDao orderDao, StoreDao storeDao, CouponDao couponDao, MenuDao menuDao) {
        this.orderDao = orderDao;
        this.storeDao = storeDao;
        this.couponDao = couponDao;
        this.menuDao = menuDao;
    }

    public PostOrderRes postOrder(PostOrderReq postOrderReq) throws BaseException {
        if (storeDao.checkStore(postOrderReq.getStoreIdx()) == 0) {
            throw new BaseException(STORES_NOT_FOUND); // 가게 존재 확인
        } else if (postOrderReq.getCouponIdx() != null) {
            if (couponDao.checkIsAvailableUserCouponInStore(postOrderReq.getCouponIdx(), postOrderReq.getStoreIdx()) == 0) {
                throw new BaseException(COUPONS_NOT_AVAILABLE); // 사용가능한 쿠폰이 맞는지 확인
            }
        }
        // 해당 메뉴가 가게에 있는지 검사
        for (PostOrderMenus orderMenu : postOrderReq.getOrderMenus()) {
            if (menuDao.checkMenuNameInStore(orderMenu.getMenuName(), postOrderReq.getStoreIdx()) == 0) {
                throw new BaseException(MENU_NOT_IN_STORES);
            }
        }
        try {
            // update UserCoupon (쿠폰사용처리)
            if (postOrderReq.getCouponIdx() != null) {
                int updatedCount = couponDao.updateUserCoupon(postOrderReq.getUserIdx(), postOrderReq.getCouponIdx());
                if (updatedCount != 1) {
                    throw new BaseException(FAILED_TO_UPDATE_USER_COUPON);
                }
            }
            // insert Order
            int createdIdx = orderDao.insertOrder(postOrderReq);

            // insert orderMenus
            for (PostOrderMenus orderMenus : postOrderReq.getOrderMenus()) {
                orderDao.insertOrderMenu(orderMenus);
            }
            PostOrderRes postOrderRes = new PostOrderRes(createdIdx);
            return postOrderRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
