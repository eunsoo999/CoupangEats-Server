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
    NOT_LOGIN_STATUS(false, 2004, "로그인 상태가 아닙니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    WRONG_AUTH_NUMBER(false, 2011, "인증번호가 일치하지 않습니다."),

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
    POST_USERS_LENGTH_EMAIL(false, 2026, "이메일 길이는 45자이하여야합니다."),

    // Address
    ADDRESSES_LENGTH_DETAILADDRESS(false, 2048, "상세 주소는 50자 이하여야합니다."),
    ADDRESSES_LENGTH_ADDRESS(false, 2049, "주소는 50자 이하여야합니다."),
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
    COUPONS_EMPTY_NUMBER(false, 2060, "쿠폰 번호를 입력해주세요."),
    COUPONS_LENGTH_NUMBER(false, 2061, "쿠폰번호는 8자리 혹은 16자리만 가능합니다."),
    POST_ORDERS_EMPTY_ADDRESS(false, 2062, "배달지 주소를 입력해주세요."),
    POST_ORDERS_LENGTH_ADDRESS(false, 2063, "배달지 주소는 100자 이하만 가능합니다."),
    POST_ORDERS_EMPTY_STOREIDX(false, 2064, "가게 번호를 입력해주세요."),
    POST_ORDERMENUS_EMPTY_MENUIDX(false, 2065, "주문 메뉴 번호를 입력해주세요."),
    POST_ORDERS_EMPTY_ORDERMENUS(false, 2066, "주문 메뉴가 비었습니다."),
    POST_ORDERMENUS_EMPTY_NAME(false, 2067, "주문 메뉴의 이름을 입력해주세요."),
    POST_ORDERMENUS_LENGTH_NAME(false, 2068, "주문 메뉴의 이름은 45자 이하만 가능합니다."),
    POST_ORDERMENUS_LENGTH_DETAIL(false, 2069, "주문 메뉴 상세는 150자 이하만 가능합니다."),
    POST_ORDERMENUS_NOT_ZERO(false, 2070, "주문 수량을 확인해주세요. 1개 이상 주문 가능합니다."),
    POST_ORDERS_EMPTY_ORDERPRICE(false, 2071, "주문 금액을 입력해주세요."),
    POST_ORDERS_EMPTY_DELIVERYPRICE(false, 2072, "배달비를 입력해주세요."),
    POST_ORDERS_EMPTY_DISCOUNTPRICE(false, 2073, "할인금액을 입력해주세요."),
    POST_ORDERS_EMPTY_TOTALPRICE(false, 2074, "총 주문가격을 입력해주세요."),
    POST_ORDERS_EMPTY_ECHOPRODUCT(false, 2075, "일회용품 수저 유무를 선택해주세요."),
    POST_ORDERS_INVALID_ECHOPRODUCT(false, 2076, "일회용품 수저 유무 값이 잘못되었습니다."),
    POST_COUPONS_USERIDX(false, 2077, "쿠폰을 지급받을 유저 번호를 입력해주세요."),
    POST_COUPONS_COUPONIDX(false, 2078, "지급받을 쿠폰 번호를 입력해주세요."),
    STORE_REVIEWS_INVALID_TYPE(false, 2079, "type이 잘못되었습니다."),
    STORE_REVIEWS_INVALID_SORT(false, 2080, "가능하지않은 정렬입니다."),
    POST_ORDERS_EMPTY_PAYTYPE(false, 2081, "결제수단을 입력해주세요."),
    POST_ORDERS_EMPTY_USERIDX(false, 2082, "유저 번호를 확인해주세요."),
    POST_ORDERMENUS_TOTALPRICE(false, 2083, "메뉴의 금액을 입력해주세요."),
    POST_ORDERS_LENGTH_STOREREQUEST(false, 2084, "가게 요청사항은 50자 이하만 가능합니다."),
    POST_ORDERS_LENGTH_DELIVERYREQUEST(false, 2085, "배달 요청사항은 50자 이하만 가능합니다."),
    STORES_EMPTY_CATEGORY(false, 2086, "조회할 카테고리를 입력해주세요."),
    STORES_INVALID_CATEGORY(false, 2087, "유효한 가게 카테고리가 아닙니다."),


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

    COUPON_NUMBER_NOT_FOUND(false, 3055, "잘못된 쿠폰번호입니다."),
    COUPONS_USED(false, 3056, "이미 지급받은 쿠폰입니다."),
    USERS_NOT_FOUND_COUPONS(false, 3057, "해당 쿠폰을 가지고 있지않은 유저입니다."),
    COUPONS_NOT_AVAILABLE(false, 3058, "사용가능한 쿠폰이 아닙니다."),

    CART_EMPTY_ADDRESS(false, 3058, "배달받을 주소를 설정해 주세요."),
    CART_IMPOSSIBLE_DISTANCE(false, 3059, "거리가 멀어 배달이 불가능합니다."),

    COUPONS_NOT_FOUND(false, 3060, "존재하지않는 쿠폰입니다."),
    COUPONS_NOT_IN_STORES(false, 3061, "가게의 쿠폰이 아니거나, 지급 만료된 쿠폰입니다."),


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
    FAILED_TO_UPDATE_USER_ADDRESS(false, 4022, "주소 선택에 실패하였습니다."),
    FAILED_TO_UPDATE_USER_COUPON(false, 4023, "쿠폰 사용에 실패하였습니다.");

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
