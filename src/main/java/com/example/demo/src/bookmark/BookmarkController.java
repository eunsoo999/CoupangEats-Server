package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.bookmark.model.GetBookmarkRes;
import com.example.demo.src.bookmark.model.PatchBookmarksStatusReq;
import com.example.demo.src.bookmark.model.PostBookmarkReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("")
public class BookmarkController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final BookmarkProvider bookmarkProvider;
    @Autowired
    private final BookmarkService bookmarkService;

    public BookmarkController(JwtService jwtService, BookmarkProvider bookmarkProvider, BookmarkService bookmarkService) {
        this.jwtService = jwtService;
        this.bookmarkProvider = bookmarkProvider;
        this.bookmarkService = bookmarkService;
    }

    /**
     * 47. 가게 즐겨찾기 조회 및 정렬 API
     * [GET] /users/:userIdx/bookmarks?sort=
     * @return BaseResponse<List<GetCouponsRes>>
     */
    @ResponseBody
    @GetMapping("/users/{userIdx}/bookmarks")
    public BaseResponse<List<GetBookmarkRes>> getBookmarks(@PathVariable int userIdx, @RequestParam String sort) {
        if (sort.isEmpty()) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_SORT);
        } else if (!sort.equalsIgnoreCase("recentAdd")) {
            return new BaseResponse<>(BOOKMARKS_INVALID_SORT);
        }

        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetBookmarkRes> getBookmarkRes = bookmarkProvider.getBookmarks(userIdx, sort);
            return new BaseResponse<>(getBookmarkRes);
        } catch (BaseException exception) {
            logger.warn("#47. " + exception.getStatus().getMessage());
            logger.warn("(userIdx : " + userIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 48. 가게 즐겨찾기 추가 API
     * [POST] /bookmarks
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PostMapping("/bookmarks")
    public BaseResponse<Map> postBookmarks(@RequestBody PostBookmarkReq postBookmarkReq) {
        if (postBookmarkReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID); // 유저번호 입력값 검증
        } else if (postBookmarkReq.getStoreIdx() == null) {
            return new BaseResponse<>(STOREIDX_EMPTY); // 가게번호 입력값 검증
        }

        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(postBookmarkReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Map<String, Integer> result = new HashMap<>();
            int createdIdx = bookmarkService.createBookmarks(postBookmarkReq);
            result.put("createdIdx", createdIdx);

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#48. " + exception.getStatus().getMessage());
            logger.warn(postBookmarkReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 49. 가게 즐겨찾기 삭제 API
     * [POST] /users/:userIdx/bookmarks/status
     * @return BaseResponse<Map>
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/bookmarks/status")
    public BaseResponse<Map> patchBookmarksStatus(@RequestBody PatchBookmarksStatusReq patchBookmarksStatusReq, @PathVariable Integer userIdx) {
        if (userIdx == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID); // 유저번호 입력값 검증
        } else if (patchBookmarksStatusReq.getStoreIdxList().isEmpty()) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_STOREIDX); // 가게번호 입력값 검증
        }

        try {
            //jwt 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Map<String, Integer> result = new HashMap<>();
            bookmarkService.updateBookmarksStatus(patchBookmarksStatusReq, userIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#49. " + exception.getStatus().getMessage());
            logger.warn("userIdx : " + userIdx + ",  " + patchBookmarksStatusReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
