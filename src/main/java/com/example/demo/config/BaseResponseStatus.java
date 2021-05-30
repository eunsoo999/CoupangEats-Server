package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EMPTY_PHONE(false, 2017, "전화번호를 입력해주세요."),
    POST_USERS_INVALID_PHONE(false, 2018, "전화번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2019, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2020, "비밀번호는 영문/숫자/특수문자 2가지 이상 조합 8~20자로 입력해주세요."),
    POST_USERS_INVALID_PASSWORD_SEQ(false, 2021, "비밀번호 3개 이상 연속되거나 동일한 문자/숫자는 불가능합니다."),
    POST_USERS_INVALID_PASSWORD_ID(false, 2022, "비밀번호는 아이디(이메일) 제외여야합니다."),
    POST_USERS_EMPTY_USERNAME(false, 2023, "이름을 입력해주세요."),
    POST_USERS_LENGTH_USERNAME(false, 2024, "이름의 길이는 2자 이상 30자 이하여야합니다."),
    POST_USERS_INVALID_USERNAME(false, 2025, "이름을 정확히 입력해주세요."),

    // Address
    ADDRESSES_EMPTY_ADDRESS(false, 2050, "주소를 입력해주세요."),
    ADDRESSES_EMPTY_ROADADDRESS(false, 2051, "도로명주소를 입력해주세요."),
    ADDRESSES_EMPTY_ALIASTYPE(false, 2052, "별칭 타입을 입력해주세요."),
    ADDRESSES_INVALID_ALIASTYPE(false, 2053, "별칭 타입 형태가 잘못되었습니다."),
    ADDRESSES_INVALID_TYPE(false, 2054, "type이 잘못되었습니다. HOME or COMPANY만 가능합니다."),


    // Store
    STORES_EMPTY_LATITUDE(false, 2055, "잘못된 위도값입니다."),
    STORES_EMPTY_LONGITUDE(false, 2056, "잘못된 경도값입니다."),
    STORES_INVALID_SORT(false, 2057, "가능하지않은 정렬입니다."),
    STORES_INVALID_CHEETAG(false, 2058, "잘못된 치타배달 가게보기 요청입니다."),
    STORES_INVALID_COUPON(false,2059, "잘못된 할인쿠폰 가게보기 요청입니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    DUPLICATED_PHONE(false, 3014, "중복된 전화번호입니다."),
    USERS_DELETED(false, 3015, "탈퇴된 회원입니다."),
    USERS_NOT_FOUND(false, 3016, "존재하지않는 회원입니다."),
    FAILED_TO_LOGIN(false,3017,"없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_SEND_PHONE_AUTH(false, 3018, "인증번호 발송 실패하였습니다."),

    // Address
    ADDRESSES_NOT_FOUND(false, 3050, "존재하지않는 배달주소입니다."),
    ADDRESSES_NOT_FOUND_LOCATION(false, 3051, "해당 도로명 주소에 대한 정보를 찾을 수 없습니다."),
    // Store
    STORES_NOT_FOUND(false, 3052, "존재하지않는 가게입니다."),
    MENUS_NOT_FOUND(false, 3053, "존재하지않는 메뉴입니다."),
    MENU_NOT_IN_STORES(false, 3054, "해당 가게의 메뉴가 아닙니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    FAILED_TO_UPDATE_ADDRESSES(false,4020,"배달 주소 수정에 실패하였습니다."),
    FAILED_TO_UPDATE_STATUS_ADDRESSES(false, 4021, "배달 주소 삭제에 실패하였습니다."),
    FAILED_TO_UPDATE_USER_ADDRESS(false, 4022, "주소 선택에 실패하였습니다.");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
