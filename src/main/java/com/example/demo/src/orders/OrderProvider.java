package com.example.demo.src.orders;

import com.example.demo.config.BaseException;
import com.example.demo.src.coupon.CouponDao;
import com.example.demo.src.coupon.model.GetCartCoupon;
import com.example.demo.src.orders.model.*;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserAddressRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OrderProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private OrderDao orderDao;
    private StoreDao storeDao;
    private UserDao userDao;
    private CouponDao couponDao;
    private ReviewDao reviewDao;

    @Autowired
    public OrderProvider(OrderDao orderDao, StoreDao storeDao, UserDao userDao, CouponDao couponDao, ReviewDao reviewDao) {
        this.orderDao = orderDao;
        this.storeDao = storeDao;
        this.userDao = userDao;
        this.couponDao = couponDao;
        this.reviewDao = reviewDao;
    }

    public GetCartRes getCart(int userIdx, int storeIdx) throws BaseException {
        // 가게 존재 확인
        if (storeDao.checkStore(storeIdx) == 0) {
            throw new BaseException(STORES_NOT_FOUND);
        }
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        // 유저가 기본 주소를 선택한 상태인지 확인
        Integer seletedAddressIdx = userDao.selectUserAddressIdx(userIdx);
        if (seletedAddressIdx == null) {
            throw new BaseException(CART_EMPTY_ADDRESS); // 배달 주소 설정 요청 에러
        }
        GetUserAddressRes getUserAddressRes = userDao.selectUserAddress(userIdx);

        //가게 거리 확인 (4km 초과 거리면 배달 불가)
        if (storeDao.getDistanceToStore(getUserAddressRes.getLatitude(), getUserAddressRes.getLongitude(), storeIdx) > 4.0) {
            throw new BaseException(CART_IMPOSSIBLE_DISTANCE); // 배달 불가 지역
        }

        try {
            // 유저 주소, 결제수단
            GetCartRes getCartInfo = orderDao.getCartUserInfo(userIdx);
            GetCartCoupon getCartCoupon = new GetCartCoupon();
            int couponCount = couponDao.getUserCouponCountInStore(userIdx, storeIdx);
            if (couponCount != 0) {
                // 쿠폰 정보 (유저가 가진 쿠폰 목록 중에 가게에서 사용가능한 쿠폰)
                getCartCoupon = couponDao.getUserCouponInStore(userIdx, storeIdx);
                getCartCoupon.setCouponCount(couponCount);
                getCartInfo.setCoupon(getCartCoupon);
            }
            // 가게 배달비
            GetStoreRes getStoreRes = storeDao.selectStoreInfo(storeIdx);
            getCartInfo.setDeliveryPrice(getStoreRes.getDeliveryPrice());

            getCartInfo.setCoupon(getCartCoupon);
            return getCartInfo;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPastOrder> getUserPastOrders(int userIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        try {
            // 과거 주문 내역 (+ 작성된 리뷰 정보 포함)
            List<GetPastOrder> pastOrderList = orderDao.selectPastOrders(userIdx);

            // 과거 주문 내역 - 메뉴목록
            for (GetPastOrder pastOrders : pastOrderList) {
                List<GetOrderMenuReview> orderMenuList = orderDao.selectOrderMenus(pastOrders.getOrderIdx());
                pastOrders.setOrderMenus(orderMenuList);
            }

            return pastOrderList;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPreparingOrder> getUserPreparingOrders(int userIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        try {
            // 준비중 내역
            List<GetPreparingOrder> GetPreparingOrderList = orderDao.selectPreparingOrders(userIdx);

            // 준비중 내역 - 메뉴목록
            for (GetPreparingOrder preparingOrder : GetPreparingOrderList) {
                List<GetOrderMenuReview> orderMenuList = orderDao.selectOrderMenus(preparingOrder.getOrderIdx());
                preparingOrder.setOrderMenus(orderMenuList);
            }

            return GetPreparingOrderList;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetOrderRes getOrderDetail(int userIdx, int orderIdx) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (orderDao.checkOrderIdx(orderIdx) == 0) {
            throw new BaseException(ORDERS_NOT_FOUND); // 주문 존재 검증
        } else if (orderDao.checkOrderByUserIdx(orderIdx, userIdx) == 0) {
            throw new BaseException(ORDER_NOT_ORDERER); // 유저가 주문한 주문번호가 맞는지 검증
        }

        try {
            // 주문한 가게번호, 가게이름
            GetOrderRes getOrderRes = orderDao.selectOrderDetail(orderIdx);

            // 주문한 메뉴 목록
            List<GetOrderMenuRes> orderMenus = orderDao.selectOrderMenusIdxAndNameAndDetail(orderIdx);
            getOrderRes.setOrderMenus(orderMenus);

            return getOrderRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
