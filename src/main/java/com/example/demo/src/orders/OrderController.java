package com.example.demo.src.orders;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.orders.model.PostOrderMenus;
import com.example.demo.src.orders.model.PostOrderReq;
import com.example.demo.src.orders.model.PostOrderRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.user.model.PostLoginReq;
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

    public OrderController(JwtService jwtService, OrderService orderService) {
        this.jwtService = jwtService;
        this.orderService = orderService;
    }

    /**
     * 27. 주문하기 API
     * [POST] /orders
     * @return BaseResponse<PostOrderRes>
     */
    @ResponseBody
    @PostMapping("/orders")
    public BaseResponse<PostOrderRes> postOrder(@RequestBody PostOrderReq postOrderReq){
        if(postOrderReq.getAddress() == null  || postOrderReq.getAddress().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ADDRESS);
        } else if(postOrderReq.getStoreIdx() == null || postOrderReq.getStoreIdx() == 0) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_STOREIDX);
        } else if(postOrderReq.getStoreName() == null || postOrderReq.getStoreName().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_STORENAME);
        } else if(postOrderReq.getOrderMenus() == null || postOrderReq.getOrderMenus().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ORDERMENUS);
        }  else if(postOrderReq.getOrderPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ORDERPRICE);
        } else if(postOrderReq.getDeliveryPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_DELIVERYPRICE);
        } else if(postOrderReq.getDiscountPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_DISCOUNTPRICE);
        } else if(postOrderReq.getTotalPrice() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_TOTALPRICE);
        } else if(postOrderReq.getCheckEchoProduct() == null) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_ECHOPRODUCT);
        } else if(!(postOrderReq.getCheckEchoProduct().equalsIgnoreCase("Y") ||
                postOrderReq.getCheckEchoProduct().equalsIgnoreCase("N"))) {
            return new BaseResponse<>(POST_ORDERS_INVALID_ECHOPRODUCT);
        } else if(postOrderReq.getPayType() == null || postOrderReq.getPayType().isEmpty()) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_PAYTYPE);
        } else if(postOrderReq.getUserIdx() == null || postOrderReq.getUserIdx() == 0) {
            return new BaseResponse<>(POST_ORDERS_EMPTY_USERIDX);
        } else if(!postOrderReq.getOrderMenus().isEmpty()) {
            // 담은 메뉴가 있는지 검사
            for (PostOrderMenus menus : postOrderReq.getOrderMenus()) {
                if(menus.getMenuName() == null || menus.getMenuName().isEmpty()) {
                    return new BaseResponse<>(POST_ORDERMENUS_EMPTY_NAME);
                } else if(menus.getCount() == null || menus.getCount() == 0) {
                    return new BaseResponse<>(POST_ORDERMENUS_NOT_ZERO);
                } else if(menus.getTotalPrice() == null) {
                    return new BaseResponse<>(POST_ORDERMENUS_TOTALPRICE);
                }
            }
        }

        try{
            // 유저 JWT 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != postOrderReq.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostOrderRes postOrderRes = orderService.postOrder(postOrderReq);
            return new BaseResponse<>(postOrderRes);
        } catch (BaseException exception){
            logger.warn("#27 " + exception.getStatus().getMessage());
            logger.warn(postOrderReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
