package com.example.demo.src.orders;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.orders.model.GetCartRes;
import com.example.demo.src.orders.model.PostOrderMenus;
import com.example.demo.src.orders.model.PostOrderReq;
import com.example.demo.src.orders.model.PostOrderRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("")
public class OrderController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final OrderProvider orderProvider;

    public OrderController(JwtService jwtService, OrderService orderService, OrderProvider orderProvider) {
        this.jwtService = jwtService;
        this.orderService = orderService;
        this.orderProvider = orderProvider;
    }

    /**
     * 26. 카트보기 API
     * [GET] /users/:userIdx/stores/:storeIdx/cart
     *
     * @return BaseResponse<GetCartRes>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/stores/{storeIdx}/cart")
    public BaseResponse<GetCartRes> getCart(@PathVariable int userIdx, @PathVariable int storeIdx) {
        try {
            // 유저 JWT 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetCartRes getCartRes = orderProvider.getCart(userIdx, storeIdx);
            return new BaseResponse<>(getCartRes);
        } catch (BaseException exception) {
            logger.warn("#26 " + exception.getStatus().getMessage());
            logger.warn("(userIdx : " + userIdx + ", storeIdx : " + storeIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 27. 주문하기 API
     * [POST] /orders
     *
     * @return BaseResponse<PostOrderRes>
     */
    @ResponseBody
    @PostMapping("/orders")
    public BaseResponse<PostOrderRes> postOrder(@RequestBody PostOrderReq postOrderReq) {
        if (postOrderReq.getAddress() == null || postOrderReq.getAddress().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ADDRESS);
        } else if (postOrderReq.getAddress().length() >= 100) {
            return new BaseResponse<>(POST_ORDERS_LENGTH_ADDRESS);
        } else if (postOrderReq.getStoreIdx() == null || postOrderReq.getStoreIdx() == 0) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_STOREIDX);
        } else if (postOrderReq.getOrderMenus() == null || postOrderReq.getOrderMenus().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ORDERMENUS);
        } else if (postOrderReq.getOrderPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ORDERPRICE);
        } else if (postOrderReq.getDeliveryPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_DELIVERYPRICE);
        } else if (postOrderReq.getDiscountPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_DISCOUNTPRICE);
        } else if (postOrderReq.getTotalPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_TOTALPRICE);
        } else if (postOrderReq.getCheckEchoProduct() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ECHOPRODUCT);
        } else if (!(postOrderReq.getCheckEchoProduct().equalsIgnoreCase("Y") ||
                postOrderReq.getCheckEchoProduct().equalsIgnoreCase("N"))) {
            return new BaseResponse<>(POST_ORDERS_INVALID_ECHOPRODUCT);
        } else if (postOrderReq.getPayType() == null || postOrderReq.getPayType().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_PAYTYPE);
        } else if (postOrderReq.getUserIdx() == null || postOrderReq.getUserIdx() == 0) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_USERIDX);
        } else if (postOrderReq.getStoreRequests() != null && postOrderReq.getStoreRequests().length() > 50) {
            return new BaseResponse<>(POST_ORDERS_LENGTH_STOREREQUEST);
        } else if (postOrderReq.getDeliveryRequests() != null && postOrderReq.getDeliveryRequests().length() > 50) {
            return new BaseResponse<>(POST_ORDERS_LENGTH_DELIVERYREQUEST);
        } else if (!postOrderReq.getOrderMenus().isEmpty()) {
            // 담은 메뉴가 있는지 검사
            for (PostOrderMenus menus : postOrderReq.getOrderMenus()) {
                if (menus.getMenuIdx() == null) {
                    return new BaseResponse<>(POST_ORDERMENUS_EMPTY_MENUIDX);
                } else if (menus.getMenuName() == null || menus.getMenuName().isEmpty()) {
                    return new BaseResponse<>(POST_ORDERMENUS_EMPTY_NAME);
                } else if (menus.getMenuName() != null && menus.getMenuName().length() > 45) {
                    return new BaseResponse<>(POST_ORDERMENUS_LENGTH_NAME);
                } else if (menus.getMenuDetail() != null && menus.getMenuDetail().length() > 150) {
                    return new BaseResponse<>(POST_ORDERMENUS_LENGTH_DETAIL);
                } else if (menus.getCount() == null || menus.getCount() == 0) {
                    return new BaseResponse<>(POST_ORDERMENUS_NOT_ZERO);
                } else if (menus.getTotalPrice() == null) {
                    return new BaseResponse<>(POST_ORDERMENUS_TOTALPRICE);
                }
            }
        }

        try {
            // 유저 JWT 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != postOrderReq.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostOrderRes postOrderRes = orderService.postOrder(postOrderReq);
            return new BaseResponse<>(postOrderRes);
        } catch (BaseException exception) {
            logger.warn("#27 " + exception.getStatus().getMessage());
            logger.warn(postOrderReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
