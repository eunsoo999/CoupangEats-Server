package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetAddressDetailRes;
import com.example.demo.src.address.model.GetAddressesRes;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.src.address.model.PostAddressRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/addresses")
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
     * 9. 배달 주소 추가 API
     * [POST] /addresses
     * @return BaseResponse<PostAddressRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostAddressRes> postAddress(@RequestBody PostAddressReq postAddressReq) {
        if (postAddressReq.getAddress() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ADDRESS);
        } else if (postAddressReq.getRoadAddress() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ROADADDRESS);
        } else if (postAddressReq.getAliasType() == null) {
            return new BaseResponse<>(ADDRESSES_EMPTY_ALIASTYPE);
        }
        else if (postAddressReq.getAliasType() != null && !(postAddressReq.getAliasType().equalsIgnoreCase("HOME")
                || postAddressReq.getAliasType().equalsIgnoreCase("COMPANY")
                || postAddressReq.getAliasType().equalsIgnoreCase("ETC")
                || postAddressReq.getAliasType().equalsIgnoreCase("NONE"))) {
            return new BaseResponse<>(ADDRESSES_INVALID_ALIASTYPE);
        } // 별칭 타입 검증

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
     * 10. 유저의 배달 주소 조회 API
     * [GET] /addresses/:addressIdx/users/:userIdx
     * @return BaseResponse<GetAddressDetailRes>
     */
    @ResponseBody
    @GetMapping("/{addressIdx}/users/{userIdx}")
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
}