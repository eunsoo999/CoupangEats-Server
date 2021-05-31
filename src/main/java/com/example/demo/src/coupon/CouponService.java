package com.example.demo.src.coupon;

import com.example.demo.config.BaseException;
import com.example.demo.src.coupon.model.PostCouponReq;
import com.example.demo.src.coupon.model.PostCouponRes;
import com.example.demo.src.coupon.model.PostUserCouponReq;
import com.example.demo.src.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class CouponService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CouponDao couponDao;
    private final UserDao userDao;

    @Autowired
    public CouponService(CouponDao couponDao,UserDao userDao) {
        this.couponDao = couponDao;
        this.userDao = userDao;
    }

    public PostCouponRes createUserCouponByCouponNumber(PostCouponReq postCouponReq) throws BaseException {
        if(userDao.checkUserIdx(postCouponReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 확인
        } else if (couponDao.checkCouponByCouponNumber(postCouponReq.getCouponNumber()) == 0) {
            throw new BaseException(COUPON_NUMBER_NOT_FOUND); // 쿠폰 번호 확인
        } else if (couponDao.checkUserCouponByCouponNumber(postCouponReq.getCouponNumber(), postCouponReq.getUserIdx()) == 1) {
            throw new BaseException(COUPONS_USED); // 해당 쿠폰을 이미 유저가 가지고 있는지 확인
        }

        try {
            int couponIdx = couponDao.selectCouponByCouponNumber(postCouponReq.getCouponNumber());
            int createdIdx = couponDao.insertUserCoupon(couponIdx, postCouponReq.getUserIdx());
            PostCouponRes postCouponRes = new PostCouponRes(createdIdx);
            return postCouponRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostCouponRes createUserCouponByStore(PostUserCouponReq postUserCouponReq, int storeIdx) throws BaseException {
        if(userDao.checkUserIdx(postUserCouponReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 확인
        } else if (couponDao.checkCoupon(postUserCouponReq.getCouponIdx()) == 0) {
            throw new BaseException(COUPONS_NOT_FOUND); // 쿠폰 존재 확인
        } else if (couponDao.checkCouponByStoreIdx(storeIdx) == 0) {
            throw new BaseException(COUPONS_NOT_IN_STORES); //해당 쿠폰이 해당 가게의 쿠폰인지 확인
        } else if (couponDao.checkUserCoupon(postUserCouponReq.getCouponIdx(), postUserCouponReq.getUserIdx()) == 1) {
            throw new BaseException(COUPONS_USED); // 해당 쿠폰을 이미 유저가 가지고 있는지 확인
        }

        try {
            int createdIdx = couponDao.insertUserCoupon(postUserCouponReq.getCouponIdx(), postUserCouponReq.getUserIdx());
            PostCouponRes postCouponRes = new PostCouponRes(createdIdx);
            return postCouponRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
