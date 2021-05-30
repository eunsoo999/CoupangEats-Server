package com.example.demo.src.coupon;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.coupon.model.GetCouponsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("")
public class CouponController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CouponProvider couponProvider;
    @Autowired
    private final JwtService jwtService;

    public CouponController(CouponProvider couponProvider, JwtService jwtService) {
        this.couponProvider = couponProvider;
        this.jwtService = jwtService;
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
