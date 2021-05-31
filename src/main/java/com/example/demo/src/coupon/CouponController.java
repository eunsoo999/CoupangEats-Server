package com.example.demo.src.coupon;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.coupon.model.GetCouponsRes;
import com.example.demo.src.coupon.model.PostCouponReq;
import com.example.demo.src.coupon.model.PostCouponRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("")
public class CouponController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CouponProvider couponProvider;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final CouponService couponService;

    public CouponController(CouponProvider couponProvider, JwtService jwtService, CouponService couponService) {
        this.couponProvider = couponProvider;
        this.jwtService = jwtService;
        this.couponService = couponService;
    }

    /**
     * 29. 가게에서 사용가능한 쿠폰 전체 조회 API
     * [GET] /users/:userIdx/coupons/stores/:storeIdx
     * @return BaseResponse<List<GetCouponsRes>>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/coupons/stores/{storeIdx}")
    public BaseResponse<List<GetCouponsRes>> postCouponByCouponNumber(@PathVariable int userIdx, @PathVariable int storeIdx) {
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetCouponsRes> getCouponsRes = couponProvider.getUserCouponsInStore(userIdx, storeIdx);
            return new BaseResponse<>(getCouponsRes);
        } catch (BaseException exception) {
            logger.warn("#29. " + exception.getStatus().getMessage());
            logger.warn("(userIdx : " + userIdx + ", " + storeIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 30. 할인쿠폰 등록 API
     * [POST] /coupons
     * @return BaseResponse<PostCouponRes>
     */
    @ResponseBody
    @PostMapping("/coupons")
    public BaseResponse<PostCouponRes> postCouponByCouponNumber(@RequestBody PostCouponReq postCouponReq) {
        if(postCouponReq.getCouponNumber() == null || postCouponReq.getCouponNumber().isEmpty()) {
            return new BaseResponse<>(COUPONS_EMPTY_NUMBER);
        } else if(!(postCouponReq.getCouponNumber().length() == 8 || postCouponReq.getCouponNumber().length() == 16)) {
            return new BaseResponse<>(COUPONS_LENGTH_NUMBER);
        }
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(postCouponReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCouponRes createdCoupon = couponService.postCoupon(postCouponReq);
            return new BaseResponse<>(createdCoupon);
        } catch (BaseException exception) {
            logger.warn("#30. " + exception.getStatus().getMessage());
            logger.warn("(" + postCouponReq.toString() + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 31. My이츠-할인쿠폰 조회 API
     * [GET] /users/:userIdx/coupons
     * @return BaseResponse<List<GetCouponsRes>>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/coupons")
    public BaseResponse<List<GetCouponsRes>> getUser(@PathVariable int userIdx) {
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetCouponsRes> getUserRes = couponProvider.getCoupons(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            logger.warn("#29. " + exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
