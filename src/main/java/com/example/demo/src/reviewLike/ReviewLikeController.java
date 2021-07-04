package com.example.demo.src.reviewLike;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.PostReviewLikeReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

@Controller
@RequestMapping("")
public class ReviewLikeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final ReviewLikeService reviewLikeService;

    public ReviewLikeController(JwtService jwtService, ReviewLikeService reviewLikeService) {
        this.jwtService = jwtService;
        this.reviewLikeService = reviewLikeService;
    }

    /**
     * 42. 리뷰 도움 돼요 API
     * [POST] /like
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PostMapping("/like")
    public BaseResponse<Map> postReviewLike(@RequestBody PostReviewLikeReq postReviewLikeReq) {
        if (postReviewLikeReq.getUserIdx() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_USERIDX);
        } else if (postReviewLikeReq.getReviewIdx() == null) {
            return new BaseResponse<>(REVIEWLIKES_EMPTY_REVIEWIDX);
        }

        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (postReviewLikeReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Integer createdReivewLikeIdx = reviewLikeService.createReviewLike(postReviewLikeReq);
            Map<String, Integer> result = new HashMap<>();
            if (createdReivewLikeIdx != null) {
                result.put("createdReivewLikeIdx", createdReivewLikeIdx);
            }
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#42. " + exception.getStatus().getMessage());
            logger.warn(postReviewLikeReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 43. 리뷰 도움 안돼요 API
     * [POST] /unlike
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PostMapping("/unlike")
    public BaseResponse<Map> postReviewUnLike(@RequestBody PostReviewLikeReq postReviewLikeReq) {
        if(postReviewLikeReq.getUserIdx() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_USERIDX);
        } else if(postReviewLikeReq.getReviewIdx() == null) {
            return new BaseResponse<>(REVIEWLIKES_EMPTY_REVIEWIDX);
        }

        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (postReviewLikeReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Integer createdReivewLikeIdx = reviewLikeService.createReviewUnLike(postReviewLikeReq);
            Map<String, Integer> result = new HashMap<>();
            if (createdReivewLikeIdx != null) {
                result.put("createdReivewLikeIdx", createdReivewLikeIdx);
            }
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#43. " + exception.getStatus().getMessage());
            logger.warn(postReviewLikeReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 44. 리뷰 도움 돼요/안돼요 취소 API
     * [Patch] /users/:userIdx/reviews/:reviewIdx/like/status
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/reviews/{reviewIdx}/like/status")
    public BaseResponse<Map> patchReviewLikeStatus(@PathVariable int userIdx, @PathVariable int reviewIdx) {
        try {
            // jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            reviewLikeService.updateReviewLikeStatus(userIdx, reviewIdx);
            Map<String, Integer> result = new HashMap<>();
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#44. " + exception.getStatus().getMessage());
            logger.warn("userIdx : " + userIdx + ", reviewIdx : " + reviewIdx);
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
