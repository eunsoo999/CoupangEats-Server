package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("")
public class AddressController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AddressService addressService;
    @Autowired
    private final AddressProvider addressProvider;
    @Autowired
    private final JwtService jwtService;

    public AddressController(AddressService addressService, AddressProvider addressProvider, JwtService jwtService) {
        this.addressService = addressService;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;
    }

    /**
     * 8. 유저의 배달 주소 목록 조회 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressesRes>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/addresses")
    public BaseResponse<GetAddressesRes> getUserAddresses(@PathVariable int userIdx) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetAddressesRes getAddressesRes = addressProvider.getAddreses(userIdx);
            return new BaseResponse<>(getAddressesRes);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 9. 배달 주소 추가 API
     * [POST] /addresses
     * @return BaseResponse<PostAddressRes>
     */
    @ResponseBody
    @PostMapping("/addresses")
    public BaseResponse<PostAddressRes> postAddress(@RequestBody PostAddressReq postAddressReq) {
        if (postAddressReq.getAddress() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ADDRESS);
        } else if (postAddressReq.getRoadAddress() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ROADADDRESS);
        } else if (postAddressReq.getAliasType() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ALIASTYPE);
        } else if (postAddressReq.getAliasType() != null && !(postAddressReq.getAliasType().equalsIgnoreCase("HOME")
                || postAddressReq.getAliasType().equalsIgnoreCase("COMPANY")
                || postAddressReq.getAliasType().equalsIgnoreCase("ETC"))) {
            return new BaseResponse<>(ADDRESSES_INVALID_ALIASTYPE);
        } else if (postAddressReq.getLatitude().intValue() >= 100) {
            return new BaseResponse<>(ADDRESSES_EMPTY_LATITUDE);
        } else if (postAddressReq.getLongitude().intValue() >= 1000) {
            return new BaseResponse<>(ADDRESSES_EMPTY_LONGITUDE);
        }

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postAddressReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int createdIdx = addressService.createAddress(postAddressReq);
            PostAddressRes postAddressRes = new PostAddressRes(createdIdx);
            return new BaseResponse<>(postAddressRes);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn(postAddressReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 10. 배달 주소 조회 API
     * [GET] /users/:userIdx/addresses/:addressIdx
     * @return BaseResponse<GetAddressDetailRes>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/addresses/{addressIdx}")
    public BaseResponse<GetAddressDetailRes> getAddress(@PathVariable int userIdx, @PathVariable int addressIdx) {
        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetAddressDetailRes addressDetailRes = addressProvider.getAddress(addressIdx, userIdx);
            return new BaseResponse<>(addressDetailRes);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ", " + addressIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 11. 배달 주소 수정 API
     * [PATCH] /users/:userIdx/addresses/:addressIdx
     * @return BaseResponse<PatchAddressRes>
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/addresses/{addressIdx}")
    public BaseResponse<PatchAddressRes> patchAddress(@RequestBody PatchAddressReq patchAddressReq, @PathVariable int addressIdx, @PathVariable int userIdx) {
        // request 검증
        if (patchAddressReq.getAliasType() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ALIASTYPE);
        } else if (patchAddressReq.getAliasType() != null && !(patchAddressReq.getAliasType().equalsIgnoreCase("HOME")
                || patchAddressReq.getAliasType().equalsIgnoreCase("COMPANY")
                || patchAddressReq.getAliasType().equalsIgnoreCase("ETC"))) {
            return new BaseResponse<>(ADDRESSES_INVALID_ALIASTYPE);
        }

        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int updatedCount = addressService.updateAddress(patchAddressReq, addressIdx, userIdx);
            return new BaseResponse<>(new PatchAddressRes(updatedCount));
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn(patchAddressReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 12. 배달 주소 삭제 API
     * [PATCH] /users/:userIdx/addresses/:addressIdx/status
     * @return BaseResponse<PatchAddressRes>
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/addresses/{addressIdx}/status")
    public BaseResponse<PatchAddressRes> patchAddress(@PathVariable int addressIdx, @PathVariable int userIdx) {
        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int updatedCount = addressService.updateStatusAddress(addressIdx, userIdx);
            return new BaseResponse<>(new PatchAddressRes(updatedCount));
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn("(" + addressIdx + ", " + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
