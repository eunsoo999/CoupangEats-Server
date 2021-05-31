package com.example.demo.src.coupon;

import com.example.demo.config.BaseException;
import com.example.demo.src.coupon.model.GetCouponsRes;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CouponProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CouponDao couponDao;
    private final UserDao userDao;
    private final StoreDao storeDao;

    @Autowired
    public CouponProvider(CouponDao couponDao,UserDao userDao, StoreDao storeDao) {
        this.couponDao = couponDao;
        this.userDao = userDao;
        this.storeDao = storeDao;
    }

    public List<GetCouponsRes> getCoupons(int userIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        try {
            List<GetCouponsRes> getCouponsResList = couponDao.selectUserCoupons(userIdx);
            if (getCouponsResList.isEmpty()) {
                return null;
            } else {
                return getCouponsResList;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetCouponsRes> getUserCouponsInStore(int userIdx, int storeIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        // 가게 존재 확인
        if (storeDao.checkStore(storeIdx) == 0) {
            throw new BaseException(STORES_NOT_FOUND);
        }

        try {
            List<GetCouponsRes> getCouponsResList = couponDao.selectUserCouponsInStore(userIdx, storeIdx);
            if (getCouponsResList.isEmpty()) {
                return null;
            } else {
                return getCouponsResList;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
