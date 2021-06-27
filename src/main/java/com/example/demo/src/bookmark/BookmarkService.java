package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.AddressDao;
import com.example.demo.src.address.AddressProvider;
import com.example.demo.src.bookmark.model.GetBookmarkRes;
import com.example.demo.src.bookmark.model.PatchBookmarksStatusReq;
import com.example.demo.src.bookmark.model.PostBookmarkReq;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class BookmarkService {
    private final UserDao userDao;
    private final BookmarkDao bookmarkDao;
    private final StoreDao storeDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BookmarkService(UserDao userDao, BookmarkDao bookmarkDao, StoreDao storeDao) {
        this.userDao = userDao;
        this.bookmarkDao = bookmarkDao;
        this.storeDao = storeDao;
    }

    public int createBookmarks(PostBookmarkReq postBookmarkReq) throws BaseException {
        if (userDao.checkUserIdx(postBookmarkReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND);// 유저 존재 확인
        } else if(storeDao.checkStore(postBookmarkReq.getStoreIdx()) == 0) {
            throw new BaseException(STORES_NOT_FOUND); // 가게 존재 확인
        } else if (bookmarkDao.checkStoreInBookmarks(postBookmarkReq.getUserIdx(), postBookmarkReq.getStoreIdx()) == 1) {
            throw new BaseException(BOOKMARKS_DUPLICATED_STORE); // 즐겨찾기목록에 해당 가게가 존재하는지 확인
        }
        try {
            int createdIdx = bookmarkDao.insertBookmark(postBookmarkReq);
            return createdIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateBookmarksStatus(PatchBookmarksStatusReq patchBookmarksStatusReq, int userIdx) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);// 유저 존재 확인
        }
        for (int storeIdx : patchBookmarksStatusReq.getStoreIdxList()) {
            if (bookmarkDao.checkStoreInBookmarks(userIdx, storeIdx) == 0) {
                throw new BaseException(BOOKMARKS_NOT_FOUND_STORE); // 즐겨찾기 목록에 있는 가게인지 검증
            }
        }

        try {
            for (int storeIdx : patchBookmarksStatusReq.getStoreIdxList()) {
                bookmarkDao.updateBookmarkStatus(userIdx, storeIdx);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
