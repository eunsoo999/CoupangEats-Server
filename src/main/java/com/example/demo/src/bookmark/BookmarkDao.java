package com.example.demo.src.bookmark;

import com.example.demo.src.bookmark.model.GetBookmarkRes;
import com.example.demo.src.bookmark.model.PostBookmarkReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BookmarkDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetBookmarkRes> selectBookmarks(int userIdx, String sort) {
        String selectBookmarksQuery = "select bookmarkTable.storeIdx, bookmarkTable.storeName, FirstStoreImage.imageUrl, " +
                "if (distance > 4, null, bookmarkTable.cheetahDelivery) as 'cheetahDelivery', " +
                "(select if (count(*) = 0, null, concat(truncate(avg(rating), 1), ' (', count(*), ')')) from Review where Review.storeIdx = bookmarkTable.storeIdx and Review.status != 'N') as 'totalReview', " +
                "if (distance > 4, null, bookmarkTable.deliveryPrice) as 'deliveryPrice', " +
                "if (distance > 4, null, bookmarkTable.deliveryTime) as 'deliveryTime', " +
                "case when distance <= 0.1 then '0.1' " +
                "when distance > 4 then '배달불가능' " +
                "when distance > 0.1 then concat(distance, 'km') end 'distance' " +
                "from (select Bookmark.createdAt as 'bookmarkCreatedAt', Store.idx as 'storeIdx', Store.storeName, Store.cheetahDelivery, case when Store.deliveryPrice = 0 then '무료배달' " +
                "else concat('배달비 ', FORMAT(Store.deliveryPrice , 0), '원') " +
                "end as 'deliveryPrice', Store.deliveryTime, truncate((6371*acos(cos(radians(Address.latitude))*cos(radians(Store.latitude)) " +
                "*cos(radians(Store.longitude)-radians(Address.longitude)) +sin(radians(Address.latitude))*sin(radians(Store.latitude)))),1) as 'distance' " +
                "from Bookmark inner join Store on Bookmark.storeIdx = Store.idx join User on User.idx = Bookmark.userIdx join Address on User.addressIdx = Address.idx " +
                "where Bookmark.status != 'N' and Bookmark.userIdx = ?) as bookmarkTable " +
                "join (select storeIdx, min(idx) as 'firstReviewImage', imageUrl from StoreImage group by storeIdx) as FirstStoreImage on bookmarkTable.storeIdx = FirstStoreImage.storeIdx ";

        if(sort.equalsIgnoreCase("recentAdd")) {
            selectBookmarksQuery += "order by bookmarkTable.bookmarkCreatedAt desc ";
        } else if(sort.equalsIgnoreCase("recentOrder")) {

        } else if(sort.equalsIgnoreCase("frequentOrder")) {

        }

        return this.jdbcTemplate.query(selectBookmarksQuery,
                (rs,rowNum) -> new GetBookmarkRes(
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("imageUrl"),
                        rs.getString("cheetahDelivery"),
                        rs.getString("totalReview"),
                        rs.getString("distance"),
                        rs.getString("deliveryTime"),
                        rs.getString("deliveryPrice")), userIdx);
    }

    public int insertBookmark(PostBookmarkReq postBookmarkReq) {
        String query = "insert into Bookmark (userIdx, storeIdx) VALUES (?, ?)";
        this.jdbcTemplate.update(query, postBookmarkReq.getUserIdx(), postBookmarkReq.getStoreIdx());

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkStoreInBookmarks(int userIdx, int storeIdx) {
        String query = "select exists(select 1 from Bookmark where status = 'Y' and userIdx = ? and storeIdx = ?)";
        return this.jdbcTemplate.queryForObject(query, int.class, userIdx, storeIdx);
    }

    public int updateBookmarkStatus(int userIdx, int storeIdx) {
        String query = "update Bookmark set status = 'N' where userIdx = ? and storeIdx = ? and status = 'Y'";
        return this.jdbcTemplate.update(query, userIdx, storeIdx);
    }
}
