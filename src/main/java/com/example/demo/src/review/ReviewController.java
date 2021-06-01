package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final ReviewProvider reviewProvider;

    public ReviewController(JwtService jwtService, ReviewProvider reviewProvider) {
        this.jwtService = jwtService;
        this.reviewProvider = reviewProvider;
    }

    /**
     * 35. 가게 리뷰 조회 및 포토 리뷰 보기, 정렬 API
     * [GET] /stores/:storeIdx/reviews?type=***&sort=**&cursor=&limit=20
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
                logger.warn("#35. " + exception.getStatus().getMessage());
                logger.warn("(" + storeIdx + ", " + type + ", " + sort + ")");
                return new BaseResponse<>(exception.getStatus());
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }
}
