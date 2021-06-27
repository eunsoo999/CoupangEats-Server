package com.example.demo.src.reviewLike;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.model.PostReviewLikeReq;
import com.example.demo.src.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class ReviewLikeService {
    private final UserDao userDao;
    private final ReviewLikeDao reviewLikeDao;
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewLikeService(UserDao userDao, ReviewLikeDao reviewLikeDao, ReviewDao reviewDao) {
        this.userDao = userDao;
        this.reviewLikeDao = reviewLikeDao;
        this.reviewDao = reviewDao;
    }

    public Integer createReviewLike(PostReviewLikeReq postReviewLikeReq) throws BaseException {
        if (userDao.checkUserIdx(postReviewLikeReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(postReviewLikeReq.getReviewIdx()) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewDao.checkReviewByuserIdx(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()) == 1) {
            throw new BaseException(REVIEW_LIKES_IMPOSSIBLE_MINE); // 자신의 리뷰에는 "도움" 체크 불가능
        }

        // 이미 도움이 돼요 or 도움이 안돼요를 누른 상태인 경우
        if (reviewLikeDao.checkReviewLike(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()) == 1) {
            if (reviewLikeDao.selectReviewLikeFlag(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()).equalsIgnoreCase("Y")) {
                throw new BaseException(REVIEW_LIKES_DUPLICATED_LIKE); //이미 "도움이 돼요"를 누른 상태에서 중복으로 등록한 경우
            } else if (reviewLikeDao.selectReviewLikeFlag(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()).equalsIgnoreCase("N")) {
                //"도움이 안돼요"를 누른 상태에서 "도움이 돼요"를 선택한 경우
                try {
                    //"도움이 안돼요" -> "도움이 돼요" 변경
                    int updateCount = reviewLikeDao.updateReviewLikeFlag("Y", postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx());
                    if (updateCount == 0) {
                        throw new BaseException(FAILED_TO_UPDATE_STATUS_REVIEW_UNLIKE); // 변경한 레코드가 없는 경우
                    }
                    return null;
                } catch(Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            } else {
                throw new BaseException(REVIEW_LIKES_INVALID_DATA);
            }
        } else {
            try {
                // 도움이 돼요 등록
                int createdReviewLikeIdx = reviewLikeDao.insertReviewLike("Y", postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx());
                return createdReviewLikeIdx;
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    public Integer createReviewUnLike(PostReviewLikeReq postReviewLikeReq) throws BaseException {
        if (userDao.checkUserIdx(postReviewLikeReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(postReviewLikeReq.getReviewIdx()) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewDao.checkReviewByuserIdx(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()) == 1) {
            throw new BaseException(REVIEW_LIKES_IMPOSSIBLE_MINE); // 자신의 리뷰에는 "도움이 안돼요" 체크 불가능
        }

        // 이미 도움이 돼요 or 도움이 안돼요를 누른 상태인 경우
        if (reviewLikeDao.checkReviewLike(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()) == 1) {
            if (reviewLikeDao.selectReviewLikeFlag(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()).equalsIgnoreCase("N")) {
                throw new BaseException(REVIEW_LIKES_DUPLICATED_UNLIKE); //이미 "도움이 안돼요"를 누른 상태에서 중복으로 등록한 경우
            } else if (reviewLikeDao.selectReviewLikeFlag(postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx()).equalsIgnoreCase("Y")) {
                //"도움이 돼요"를 누른 상태에서 "도움이 안돼요"를 선택한 경우
                try {
                    //"도움이 돼요" -> "도움이 안돼요" 변경
                    int updateCount = reviewLikeDao.updateReviewLikeFlag("N", postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx());
                    if (updateCount == 0) {
                        throw new BaseException(FAILED_TO_UPDATE_STATUS_REVIEW_LIKE); // 변경한 레코드가 없는 경우
                    }
                    return null;
                } catch(Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            } else {
                throw new BaseException(REVIEW_LIKES_INVALID_DATA);
            }
        } else {
            try {
                // 도움이 안돼요 등록
                int createdReviewLikeIdx = reviewLikeDao.insertReviewLike("N", postReviewLikeReq.getUserIdx(), postReviewLikeReq.getReviewIdx());
                return createdReviewLikeIdx;
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public void updateReviewLikeStatus(int userIdx, int reviewIdx) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(reviewIdx) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewLikeDao.checkReviewLike(userIdx, reviewIdx) == 0) {
            throw new BaseException(REVIEW_LIKES_NOT_FOUND); // "도움이 돼요/안돼요" 체크상태가 아님
        }

        try {
            reviewLikeDao.updateReviewLikeStatus(userIdx, reviewIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
