package com.example.demo.src.user;

import com.example.demo.src.address.AddressProvider;
import com.example.demo.src.address.model.GetLocationRes;
import com.example.demo.utils.KakaoApiService;
import com.example.demo.utils.SmsAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

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
     * 0. 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getEmail() == null || postUserReq.getEmail().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        } else if(postUserReq.getEmail().length() > 45) {
            return new BaseResponse<>(POST_USERS_LENGTH_EMAIL);
        } else if(!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        } else if(postUserReq.getPhone() == null || postUserReq.getPhone().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        } else if(!isRegexPhone(postUserReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        } else if(postUserReq.getPassword() == null || postUserReq.getPassword().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        } else if(!isRegexPassword(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        } else if(postUserReq.getPassword().contains(postUserReq.getEmail().split("@")[0])) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD_ID);
        } else if(!isRegexPasswordSequence(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD_SEQ);
        } else if(postUserReq.getUserName() == null || postUserReq.getUserName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_USERNAME);
        } else if(postUserReq.getUserName().length() > 40 || postUserReq.getUserName().length() < 2) {
            return new BaseResponse<>(POST_USERS_LENGTH_USERNAME);
        } else if(!isRegexName(postUserReq.getUserName())) {
            return new BaseResponse<>(POST_USERS_INVALID_USERNAME);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            logger.warn("#0. " +exception.getStatus().getMessage());
            logger.warn(postUserReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1. 자동로그인 API
     * [GET] /users/:userIdx/auto-login
     * @return BaseResponse<>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/auto-login")
    public BaseResponse<CheckAutoLoginRes> checkAutoLogin(@PathVariable int userIdx) {
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse(NOT_LOGIN_STATUS);
            }
            return new BaseResponse<>(new CheckAutoLoginRes("Y"));
        } catch (BaseException exception) {
            logger.warn("#1. " + exception.getStatus().getMessage());
            return new BaseResponse<>(NOT_LOGIN_STATUS);
        }
    }

    /**
     * 2. 카카오 로그인 API
     * [POST] /users/login/kakao
     */
    @ResponseBody
    @PostMapping("/login/kakao")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestBody PostKakaoLoginReq postKakaoLogin) {
        if (postKakaoLogin.getAccessToken() == null || postKakaoLogin.getAccessToken().isEmpty()) {
            return new BaseResponse<>(AUTH_KAKAO_EMPTY_TOKEN);
        }

        try {
            // 액세스 토큰으로 사용자 정보 받아온다.
            KaKaoUserInfo kaKaoUserInfo = KakaoApiService.getKakaoUserInfo(postKakaoLogin.getAccessToken());

            // 로그인 처리 or 회원가입 진행 후 jwt, userIdx 반환
            PostLoginRes postLoginRes = userProvider.kakaoLogin(kaKaoUserInfo);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            logger.warn("#2. " + exception.getStatus().getMessage());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 3. 로그인 API
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
            logger.warn("#3. " +exception.getStatus().getMessage());
            logger.warn(postLoginReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 4. 이메일 중복 확인 API
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
                getDuplicatedRes = new GetDuplicatedEmailRes("Y");
            } else {
                getDuplicatedRes = new GetDuplicatedEmailRes("N");
            }
            return new BaseResponse<>(getDuplicatedRes);
        } catch (BaseException exception) {
            logger.warn("#4. " +exception.getStatus().getMessage());
            logger.warn("(" + email + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 5. 전화번호 중복 확인 API
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
                getDuplicatedPhoneRes = new GetDuplicatedPhoneRes("Y", duplicatedEmail);
            } else {
                getDuplicatedPhoneRes = new GetDuplicatedPhoneRes("N", null);
            }
            return new BaseResponse<>(getDuplicatedPhoneRes);
        } catch (BaseException exception) {
            logger.warn("#5. " +exception.getStatus().getMessage());
            logger.warn("(" + phone + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 6. 휴대폰 인증번호 발송 API
     * [POST] /users/auth/phone
     */
    @ResponseBody
    @PostMapping("/auth/phone")
    public BaseResponse<PostUserPhoneRes> sendMessage(@RequestBody PostUserPhoneReq postUserPhoneReq, HttpSession session) {
        if(postUserPhoneReq.getPhone() == null || postUserPhoneReq.getPhone().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if(!isRegexPhone(postUserPhoneReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }

        try{
            PhoneAuthInfo postUserPhoneRes = smsAuthService.sendPhoneAuth(postUserPhoneReq.getPhone()); // 문자 발송
            // 인증번호 세션 저장
            session.setMaxInactiveInterval(30*5); // 세션유지시간 = 5분
            session.setAttribute(postUserPhoneRes.getPhone(), postUserPhoneRes.getAuthNumber()); // key-value 휴대폰번호-인증번호로 세션에 저장
            return new BaseResponse<>(new PostUserPhoneRes(postUserPhoneRes.getPhone(), "5분 이내로 인증번호를 입력해주세요."));
        } catch(BaseException exception){
            logger.warn(exception.getMessage());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 7. 휴대폰 인증번호 확인 API
     * [GET] /users/auth/phone/confirm
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @PostMapping("/auth/phone/confirm")
    public BaseResponse<PostUserPhoneRes> checkAuthNumber(@RequestBody PostAuthNumberReq postAuthNumberReq, HttpSession session) {
        if(postAuthNumberReq.getPhone() == null || postAuthNumberReq.getPhone().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        } else if(!isRegexPhone(postAuthNumberReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }else if(postAuthNumberReq.getAuthNumber() == null || postAuthNumberReq.getAuthNumber().isEmpty()) {
            return new BaseResponse<>(AUTH_PHONE_EMPTY_NUMBER);
        }
        String sessionAuthNumber = (String) session.getAttribute(postAuthNumberReq.getPhone());
        if (postAuthNumberReq.getAuthNumber().equals(sessionAuthNumber)) {
            session.removeAttribute(postAuthNumberReq.getPhone()); // 인증번호가 맞다면 세션삭제
            return new BaseResponse<>(new PostUserPhoneRes(postAuthNumberReq.getPhone(), "인증이 완료되었습니다."));
        } else {
            return new BaseResponse<>(WRONG_AUTH_NUMBER);
        }
    }

    /**
     * 8. 로그인 유저 정보 조회 API
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
            logger.warn("#8. " + exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 14. 유저의 집 / 회사 주소 존재 확인 API (home, company)
     * [GET] /users/:userIdx/addresses/check?type=
     * @return BaseResponse<GetExistAddressRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/addresses/check")
    public BaseResponse<GetExistAddressRes> checkExistsBasicAddress(@PathVariable int userIdx, @RequestParam("type") String type) {
        if (!(type.equalsIgnoreCase("HOME") || type.equalsIgnoreCase("COMPANY"))) {
            return new BaseResponse<>(ADDRESSES_INVALID_TYPE);
        }
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetExistAddressRes getExistAddressRes = userProvider.checkExistsBasicAddress(userIdx, type);
            return new BaseResponse<>(getExistAddressRes);
        } catch (BaseException exception) {
            logger.warn("#14. " + exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ", "+ type + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 15. 기본 배달 주소 선택 API 추가
     * [PATCH] /users/:userIdx/pick/addresses/:addressIdx
     * @return BaseResponse<PatchUserRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/pick/addresses/{addressIdx}")
    public BaseResponse<GetLocationRes> patchUserAddressIdx(@PathVariable int userIdx, @PathVariable int addressIdx) {
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetLocationRes result = userService.updateUserAddressIdx(userIdx, addressIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#15. " + exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ", "+ addressIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 16. 유저의 기본 배달 주소 정보 조회 API
     * [GET] /users/:userIdx/pick/addresses
     * @return BaseResponse<GetUserAddressRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/pick/addresses")
    public BaseResponse<GetUserAddressRes> getUserAddress(@PathVariable int userIdx) {
        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserAddressRes getUserAddressRes = userProvider.getUserAddress(userIdx);
            return new BaseResponse<>(getUserAddressRes);
        } catch (BaseException exception) {
            logger.warn("#16. " + exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
