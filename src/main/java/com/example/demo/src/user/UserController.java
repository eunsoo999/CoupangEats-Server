package com.example.demo.src.user;

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

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입 API
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
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
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
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
