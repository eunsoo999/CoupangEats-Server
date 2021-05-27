package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.GetMainRes;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.SearchOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexLatitude;
import static com.example.demo.utils.ValidationRegex.isRegexLongitude;


@RestController
@RequestMapping("/stores")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;

    public StoreController(StoreProvider storeProvider, StoreService storeService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
    }

    /**
     * 15. 메인 화면 API
     * [GET] /stores?lat=&lon=&sort=&cheetah=&coupon=&minOrderPrice=&midDelivery=&   cursor=&limit=20
     * @return BaseResponse<GetMainRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMainRes> getUserAddresses(@RequestParam String lat, @RequestParam String lon,
                                                     @RequestParam(required = false) String sort, @RequestParam(required = false) String cheetah,
                                                     @RequestParam(required = false) Integer midDelivery, @RequestParam(required = false) Integer minOrderPrice,
                                                     @RequestParam(required = false) String coupon) {
        if(!isRegexLatitude(lat)) {
            return new BaseResponse<>(STORES_EMPTY_LATITUDE);
        } else if(!isRegexLongitude(lon)) {
            return new BaseResponse<>(STORES_EMPTY_LONGITUDE);
        } else if(sort != null && !(sort.equalsIgnoreCase("recomm") || sort.equalsIgnoreCase("orders")
                || sort.equalsIgnoreCase("nearby") || sort.equalsIgnoreCase("rating") || sort.equalsIgnoreCase("new"))) {
            return new BaseResponse<>(STORES_INVALID_SORT);
        } else if(cheetah != null && !cheetah.equalsIgnoreCase("Y")) {
            return new BaseResponse<>(STORES_INVALID_CHEETAG);
        } else if(coupon != null && !coupon.equalsIgnoreCase("Y")) {
            return new BaseResponse<>(STORES_INVALID_COUPON);
        }

        SearchOption searchOption = new SearchOption(lat, lon, sort, cheetah, midDelivery, minOrderPrice, coupon);

        try {
            GetMainRes mainStores = storeProvider.getMainStores(searchOption);
            return new BaseResponse<>(mainStores);
        } catch (BaseException exception) {
            logger.warn("#15. " + exception.getStatus().getMessage());
            logger.warn(searchOption.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 16. 가게 카테고리 조회 API
     * [GET] /stores/categories
     * @return BaseResponse<GetStoreCategoryRes>
     */
    @ResponseBody
    @GetMapping("/categories")
    public BaseResponse<List<GetStoreCategoryRes>> getStoreCategories() {
        try {
            List<GetStoreCategoryRes> getStoreCategories = storeProvider.getStoreCategories();
            return new BaseResponse<>(getStoreCategories);
        } catch (BaseException exception) {
            logger.warn("#16. " + exception.getStatus().getMessage());
            return new BaseResponse<>(exception.getStatus());
        }

    }

}
