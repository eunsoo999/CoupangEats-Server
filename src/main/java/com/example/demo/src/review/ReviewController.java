package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexImage;

@RestController
@RequestMapping("")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;

    public ReviewController(JwtService jwtService, ReviewProvider reviewProvider, ReviewService reviewService) {
        this.jwtService = jwtService;
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
    }

    /**
     * 36. 가게 리뷰 조회 및 포토 리뷰 보기, 정렬 API
     * [GET] /stores/:storeIdx/reviews?type=&sort=
     * @return BaseResponse<GetReviewsRes>
     */
    @ResponseBody
    @GetMapping("/stores/{storeIdx}/reviews")
    public BaseResponse<GetReviewsRes> getStoreReviews(@PathVariable Integer storeIdx,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) String sort) {
        if(type != null && !type.equalsIgnoreCase("photo")) {
            return new BaseResponse<>(STORE_REVIEWS_INVALID_TYPE);
        } else if(sort != null && !(sort.equalsIgnoreCase("new") || sort.equalsIgnoreCase("reviewliked")
                                        || sort.equalsIgnoreCase("rating-desc") || sort.equalsIgnoreCase("rating-asc"))) {
            return new BaseResponse<>(STORE_REVIEWS_INVALID_SORT);
        }
        try {
            try{
                // 로그인 유저의 접근인지 확인
                int userIdxByJwt = jwtService.getUserIdx();

                GetReviewsRes getReviewsRes = reviewProvider.getStoreReviews(userIdxByJwt, storeIdx, type, sort);
                return new BaseResponse<>(getReviewsRes);
            } catch (BaseException exception){
                // JWT 값이 비어있다는 에러일 경우 (비로그인유저의 접근)
                if (exception.getStatus().equals(EMPTY_JWT)) {
                    GetReviewsRes getReviewsRes = reviewProvider.getStoreReviews(null, storeIdx, type, sort);
                    return new BaseResponse<>(getReviewsRes);
                }
                // JWT 검증 에러일 경우
                logger.warn("#36. " + exception.getStatus().getMessage());
                logger.warn("(" + storeIdx + ", " + type + ", " + sort + ")");
                return new BaseResponse<>(exception.getStatus());
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 37. 리뷰 작성 API
     * [POST] /reviews
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PostMapping("/reviews")
    public BaseResponse<Map> postReview(@RequestBody PostReviewReq postReviewReq) {
        // Request 검증
        if(postReviewReq.getOrderIdx() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_ORDERIDX);
        } else if(postReviewReq.getStoreIdx() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_STOREIDX);
        } else if(postReviewReq.getRating() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_RATING);
        } else if(postReviewReq.getRating() != null && !(postReviewReq.getRating() == 1 || postReviewReq.getRating() == 2 ||
                postReviewReq.getRating() == 3 || postReviewReq.getRating() == 4 || postReviewReq.getRating() == 5 )) {
            return new BaseResponse<>(REVIEWS_INVALID_RATING);
        } else if(postReviewReq.getContents() == null || postReviewReq.getContents().isEmpty()) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENTS);
        } else if(postReviewReq.getContents() != null && postReviewReq.getContents().length() > 300) {
            return new BaseResponse<>(REVIEWS_LENGTH_CONTENTS);
        } else if(postReviewReq.getDeliveryLiked() != null
                && !(postReviewReq.getDeliveryLiked().equalsIgnoreCase("GOOD")
                || postReviewReq.getDeliveryLiked().equalsIgnoreCase("BAD"))) {
            return new BaseResponse<>(REVIEWS_INVALID_DELIVERY_LIKED);
        }  else if(postReviewReq.getDeliveryComment() != null && postReviewReq.getDeliveryComment().length() > 80) {
            return new BaseResponse<>(REVIEWS_LENGTH_DELIVERY);
        } else if(postReviewReq.getUserIdx() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_USERIDX);
        }
        if(postReviewReq.getImages() != null) {
            for (String url : postReviewReq.getImages()) {
                if(!isRegexImage(url)) {
                    return new BaseResponse<>(INVALID_IMAGE_URL);
                }
            }
        }
        if(postReviewReq.getMenuReviews() != null) {
            for (PostMenuReviewReq menuReview : postReviewReq.getMenuReviews()) {
                if(menuReview.getOrderMenuIdx() == null) {
                    return new BaseResponse<>(REVIEWS_EMPTY_MENUIDX);
                }
                if(menuReview.getMenuComment() != null && menuReview.getMenuComment().length() > 80) {
                    return new BaseResponse<>(REVIEWS_LENGTH_MENU);
                }
                if(menuReview.getMenuLiked() != null && !(menuReview.getMenuLiked().equalsIgnoreCase("GOOD")
                                                                || menuReview.getMenuLiked().equalsIgnoreCase("BAD"))) {
                    return new BaseResponse<>(REVIEWS_INVALID_MENU_LIKED);
                }
            }
        }

        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (postReviewReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int createdIdx = reviewService.createReview(postReviewReq);
            Map<String, Integer> result = new HashMap<>();
            result.put("createdIdx", createdIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#37. " +exception.getStatus().getMessage());
            logger.warn(postReviewReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 39. 리뷰 삭제 API
     * [PATCH] /users/:userIdx/reviews/:reviewIdx/status
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/reviews/{reviewIdx}/status")
    public BaseResponse<Map> patchReviewStatus(@PathVariable int userIdx, @PathVariable int reviewIdx) {
        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int updatedCount = reviewService.updateStatusReview(userIdx, reviewIdx);
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#39. " +exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ", " + reviewIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 40. 리뷰 조회 API
     * [GET] /users/:userIdx/reviews/:reviewIdx
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/reviews/{reviewIdx}")
    public BaseResponse<GetReviewRes> GetReview(@PathVariable int userIdx, @PathVariable int reviewIdx) {
        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetReviewRes getReviewRes = reviewProvider.getReview(userIdx, reviewIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            logger.warn("#40. " +exception.getStatus().getMessage());
            logger.warn("(" + userIdx + ", " + reviewIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
