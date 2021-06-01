package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private final JwtService jwtService;

    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    /**
     * 16. 메인 화면 API
     * [GET] /stores?lat=&lon=&sort=&cheetah=&coupon=&minOrderPrice=&minDelivery=&   cursor=&limit=20
     * @return BaseResponse<GetMainRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMainRes> getUserAddresses(@RequestParam String lat, @RequestParam String lon,
                                                     @RequestParam(required = false) String sort, @RequestParam(required = false) String cheetah,
                                                     @RequestParam(required = false) Integer minDelivery, @RequestParam(required = false) Integer minOrderPrice,
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

        SearchOption searchOption = new SearchOption(lat, lon, sort, cheetah, minDelivery, minOrderPrice, coupon, null);

        try {
            GetMainRes mainStores = storeProvider.getMainStores(searchOption);
            return new BaseResponse<>(mainStores);
        } catch (BaseException exception) {
            logger.warn("#16. " + exception.getStatus().getMessage());
            logger.warn(searchOption.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 17. 가게 카테고리 조회 API
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
            logger.warn("#17. " + exception.getStatus().getMessage());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 18. 할인 중인 가게 전체 조회 API
     * [GET] /stores/discount?lat=&lon=
     * @return BaseResponse<GetOnSaleStoresRes>
     */
    @ResponseBody
    @GetMapping("/discount")
    public BaseResponse<GetOnSaleStoresRes> getOnSaleStores(@RequestParam String lat, @RequestParam String lon,
                                                            @RequestParam(required = false) String sort, @RequestParam(required = false) String cheetah,
                                                            @RequestParam(required = false) Integer minDelivery, @RequestParam(required = false) Integer minOrderPrice,
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

        SearchOption searchOption = new SearchOption(lat, lon, sort, cheetah, minDelivery, minOrderPrice, coupon, null);

        try {
            GetOnSaleStoresRes getOnSaleStoresList = storeProvider.getOnsaleStores(searchOption);
            return new BaseResponse<>(getOnSaleStoresList);
        } catch (BaseException exception) {
            logger.warn("#18. " + exception.getStatus().getMessage());
            logger.warn(searchOption.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 19. 새로 들어온 가게 전체 조회 API
     * [GET] /stores/new?lat=&lon=
     * @return BaseResponse<GetNewStoresRes>
     */
    @ResponseBody
    @GetMapping("/new")
    public BaseResponse<GetNewStoresRes> getNewStores(@RequestParam String lat, @RequestParam String lon,
                                                      @RequestParam(required = false) String sort, @RequestParam(required = false) String cheetah,
                                                      @RequestParam(required = false) Integer minDelivery, @RequestParam(required = false) Integer minOrderPrice,
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
        SearchOption searchOption = new SearchOption(lat, lon, sort, cheetah, minDelivery, minOrderPrice, coupon, null);
        try {
            GetNewStoresRes getNewStoresRes = storeProvider.getNewStores(searchOption);
            return new BaseResponse<>(getNewStoresRes);
        } catch (BaseException exception) {
            logger.warn("#19. " + exception.getStatus().getMessage());
            logger.warn(searchOption.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 20. 카테고리별 주변 가게 조회 및 검색 옵션 API
     * [GET] /stores/category?lat=&lon= &category= &cheetah=y &coupon=& minOrderPrice= &sort=& minDelivery==
     * @return BaseResponse<GetMainRes>
     */
    @ResponseBody
    @GetMapping("/category")
    public BaseResponse<GetStoresByCategoryRes> getStoresByCategory(@RequestParam String lat, @RequestParam String lon,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String sort, @RequestParam(required = false) String cheetah,
                                                        @RequestParam(required = false) Integer minDelivery, @RequestParam(required = false) Integer minOrderPrice,
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
        } else if(category == null) {
            return new BaseResponse<>(STORES_EMPTY_CATEGORY);
        } else if(!(category.equals("신규 맛집") || category.equals("1인분") || category.equals("한식") || category.equals("치킨")
                || category.equals("분식") || category.equals("돈까스") || category.equals("족발/보쌈") || category.equals("찜/탕")
                || category.equals("구이") || category.equals("피자") || category.equals("중식") || category.equals("일식")
                || category.equals("회/해물") || category.equals("양식") || category.equals("커피/차") || category.equals("디저트")
                || category.equals("간식") || category.equals("아시안") || category.equals("샌드위치") || category.equals("샐러드")
                || category.equals("버거") || category.equals("멕시칸") || category.equals("도시락") || category.equals("죽") || category.equals("프랜차이즈"))) {
             return new BaseResponse<>(STORES_INVALID_CATEGORY);
        }
        SearchOption searchOption = new SearchOption(lat, lon, sort, cheetah, minDelivery, minOrderPrice, coupon, category);
        try {
            GetStoresByCategoryRes mainStores = storeProvider.getStoreByCategory(searchOption);
            return new BaseResponse<>(mainStores);
        } catch (BaseException exception) {
            logger.warn("#20. " + exception.getStatus().getMessage());
            logger.warn(searchOption.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 23. 가게 조회 API
     * [GET] /stores/:storesIdx
     * @return BaseResponse<GetStoreRes>
     */
    @ResponseBody
    @GetMapping("/{storeIdx}")
    public BaseResponse<GetStoreRes> getStore(@PathVariable int storeIdx) {
        try {
            try {
                // 로그인 유저의 접근인지 확인
                int userIdxByJwt = jwtService.getUserIdx();
                GetStoreRes getStoreRes = storeProvider.getStore(storeIdx, userIdxByJwt);
                return new BaseResponse<>(getStoreRes);
            } catch (BaseException exception) {
                // JWT 값이 비어있다는 에러일 경우 (비로그인유저의 접근)
                if (exception.getStatus().equals(EMPTY_JWT)) {
                    GetStoreRes getStoreRes = storeProvider.getStore(storeIdx, null);
                    return new BaseResponse<>(getStoreRes);
                }
                // JWT 검증 에러일 경우
                logger.warn("#23. " + exception.getStatus().getMessage());
                logger.warn(String.valueOf(storeIdx));
                return new BaseResponse<>(exception.getStatus());
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
