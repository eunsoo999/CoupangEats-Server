package com.example.demo.src.user;

import com.example.demo.src.address.AddressProvider;
import com.example.demo.src.address.model.GetAddressesRes;
import com.example.demo.utils.SmsAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final SmsAuthService smsAuthService;
    @Autowired
    private final AddressProvider addressProvider;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, SmsAuthService smsAuthService, AddressProvider addressProvider){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.smsAuthService = smsAuthService;
        this.addressProvider = addressProvider;
    }

    /**
     * 로그인 유저 정보 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable int userIdx) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetUserRes getUserRes = userProvider.retrieveUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 휴대폰 인증번호 발송 API
     * [POST] /users/auth/phone
     */
    @ResponseBody
    @PostMapping("/auth/phone")
    public BaseResponse<PostUserPhoneRes> sendMessage(@RequestBody PostUserPhoneReq postUserPhoneReq) throws BaseException {
        if(postUserPhoneReq.getPhone() == null || postUserPhoneReq.getPhone().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if(!isRegexPhone(postUserPhoneReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        // 중복 체크
        try {
            if(userProvider.checkPhone(postUserPhoneReq.getPhone()) == 1) {
                return new BaseResponse<>(DUPLICATED_PHONE);
            }
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

        try{
            PostUserPhoneRes postUserPhoneRes = smsAuthService.sendPhoneAuth(postUserPhoneReq.getPhone());
            return new BaseResponse<>(postUserPhoneRes);
        } catch(BaseException exception){
            logger.warn("");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1. 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getEmail() == null || postUserReq.getEmail().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postUserReq.getPhone() == null || postUserReq.getPhone().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if(!isRegexPhone(postUserReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        if(postUserReq.getPassword() == null || postUserReq.getPassword().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(!isRegexPassword(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if(postUserReq.getPassword().contains(postUserReq.getEmail().split("@")[0])) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD_ID);
        }
        if(!isRegexPasswordSequence(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD_SEQ);
        }
        if(postUserReq.getUserName() == null || postUserReq.getUserName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_USERNAME);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            logger.warn(exception.getStatus().getMessage());
            logger.warn(postUserReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 2. 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if(postLoginReq.getEmail() == null || postLoginReq.getEmail().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(postLoginReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postLoginReq.getPassword() == null || postLoginReq.getPassword().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            logger.warn(exception.getStatus().getMessage());
            logger.warn(postLoginReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3. 이메일 중복 확인 API
     * [GET] /users/email/check?email=silver@naver.com
     * @return BaseResponse<GetDuplicatedRes>
     */
    @ResponseBody
    @GetMapping("/email/check")
    public BaseResponse<GetDuplicatedEmailRes> checkDuplicatedEmail(@RequestParam String email) {
        // request 값 확인
        if (email.isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        try {
            GetDuplicatedEmailRes getDuplicatedRes;
            // 중복된 이메일일 경우
            if (userProvider.checkEmail(email) == 1) {
                getDuplicatedRes = new GetDuplicatedEmailRes(true);
            } else {
                getDuplicatedRes = new GetDuplicatedEmailRes(false);
            }
            return new BaseResponse<>(getDuplicatedRes);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn("(" + email + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 4. 전화번호 중복 확인 API
     * [GET] /users/phone/check?phone=01029292929
     * @return BaseResponse<GetDuplicatedRes>
     */
    @ResponseBody
    @GetMapping("/phone/check")
    public BaseResponse<GetDuplicatedPhoneRes> checkDuplicatedPhone(@RequestParam String phone) {
        // request 값 확인
        if (phone.isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        try {
            GetDuplicatedPhoneRes getDuplicatedPhoneRes;
            if(userProvider.checkPhone(phone) == 1) {
                String duplicatedEmail = userProvider.getMaskingEmailByPhone(phone);
                getDuplicatedPhoneRes = new GetDuplicatedPhoneRes(true, duplicatedEmail);
            } else {
                getDuplicatedPhoneRes = new GetDuplicatedPhoneRes(false, null);
            }
            return new BaseResponse<>(getDuplicatedPhoneRes);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().getMessage());
            logger.warn("(" + phone + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 8. 로그인 유저 배달 주소 조회 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressesRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/addresses")
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
}
