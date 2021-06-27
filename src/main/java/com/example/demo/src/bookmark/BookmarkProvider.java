package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.src.bookmark.model.GetBookmarkRes;
import com.example.demo.src.bookmark.model.PostBookmarkReq;
import com.example.demo.src.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_NOT_FOUND;

@Service
public class BookmarkProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BookmarkDao bookmarkDao;
    private final UserDao userDao;

    @Autowired
    public BookmarkProvider(BookmarkDao bookmarkDao, UserDao userDao) {
        this.bookmarkDao = bookmarkDao;
        this.userDao = userDao;
    }

    public List<GetBookmarkRes> getBookmarks(int userIdx, String sort) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }

        try {
            List<GetBookmarkRes> getBookmarkRes = bookmarkDao.selectBookmarks(userIdx, sort);
            if (getBookmarkRes.isEmpty()) {
                getBookmarkRes = null;
            }
            return getBookmarkRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
