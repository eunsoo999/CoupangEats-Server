package com.example.demo.src.store;

import com.example.demo.src.store.model.GetStoreMainBox;
import com.example.demo.src.store.model.GetStoreSmallBox;
import com.example.demo.src.store.model.SearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreMainBox> selectStoreMainBoxes (SearchOption searchOption) {
        String selectAddressListQuery = "select Store.idx as 'storeIdx', Store.storeName as 'storeName', " +
                "case when Store.cheetahDelivery = 'Y' then '치타배달' " +
                "when TIMESTAMPDIFF(WEEK, Store.createdAt, CURRENT_TIMESTAMP()) <= 2 then '신규' " +
                "end as 'markIcon', " +
                "(select if (count(*) = 0, null, concat(truncate(avg(rating), 1), ' (', count(*), ')')) " +
                "from Review " +
                "where Review.storeIdx = Store.idx and Review.status != 'N') as 'totalReview', " +
                "concat(if(truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " + " + " +
                "+sin(radians(?))*sin(radians(latitude)))),1) < 0.1, '0.1', " +
                "truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " +
                "+sin(radians(?))*sin(radians(latitude)))),1)), 'km') as 'distance', " +
                "case when Store.deliveryPrice = 0 then '무료배달'" +
                "else concat('배달비 ', FORMAT(Store.deliveryPrice , 0), '원') " +
                "end as 'deliveryPrice', " +
                "Store.deliveryTime, " +
                "(select concat(Coupon.discountPrice, '원 쿠폰') " +
                "from Coupon " +
                "where Coupon.status != 'N' and now() < Coupon.ExpirationDate and Store.idx = Coupon.storeIdx limit 1) as 'coupon' " +
                "from Store " +
                "where Store.status != 'N' ";

        // 검색조건처리 - 치타배달(cheetah)보기
        if (searchOption.getCheetah() != null && searchOption.getCheetah().equalsIgnoreCase("Y")) {
            selectAddressListQuery += "and Store.cheetahDelivery = 'Y' ";
        }

        // 검색조건처리 - 배달비최소비용
        if (searchOption.getMidDelivery() != null) {
            selectAddressListQuery += "and Store.deliveryPrice <= " + searchOption.getMidDelivery() + " ";
        }

        // 검색조건처리 - 배달최소주문
        if (searchOption.getMinOrderPrice() != null) {
            selectAddressListQuery += "and Store.minOrderPrice <= " + searchOption.getMinOrderPrice() + " ";
        }

        // 검색조건처리 - 할인쿠폰유무
        if (searchOption.getCoupon() != null && searchOption.getCoupon().equalsIgnoreCase("Y")) {
            selectAddressListQuery += "and (select exists(select idx from Coupon " +
                    "where Coupon.status != 'N' and now() < Coupon.ExpirationDate " +
                    "and Store.idx = Coupon.storeIdx limit 1))" + " ";
        }

        selectAddressListQuery += "having distance < 4 ";

        // 검색조건처리 - 정렬
        if (searchOption.getSort() != null) {
            if (searchOption.getSort().equalsIgnoreCase("orders")) { // 주문많은순
                selectAddressListQuery += "order by (select count(*) from Orders where Orders.status != 'N' and Orders.storeIdx = Store.idx) desc ";
            } else if (searchOption.getSort().equalsIgnoreCase("nearby")) { // 가까운순
                selectAddressListQuery += "order by distance ";
            } else if (searchOption.getSort().equalsIgnoreCase("rating")) { // 별점높은순
                selectAddressListQuery += "order by (select avg(rating) from Review where Review.storeIdx = Store.idx and Review.status != 'N') desc ";
            } else if (searchOption.getSort().equalsIgnoreCase("new")) { // 신규매장순
                selectAddressListQuery += "order by Store.createdAt desc ";
            }
        }

        Object[] selectStoreMainBoxParams = new Object[]{searchOption.getLat(), searchOption.getLon(), searchOption.getLat(),
                searchOption.getLat(), searchOption.getLon(), searchOption.getLat()};

        return this.jdbcTemplate.query(selectAddressListQuery,
                (rs,rowNum) -> new GetStoreMainBox(
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("markIcon"),
                        rs.getString("totalReview"),
                        rs.getString("distance"),
                        rs.getString("deliveryPrice"),
                        rs.getString("deliveryTime"),
                        rs.getString("coupon")), selectStoreMainBoxParams);
    }

    public List<String> selectStoreImageUrls(int storeIdx) {
        String selectStoreImageUrlsQuery = "select imageUrl from StoreImage where storeIdx = ?";

        int selectStoreImageUrlsParams = storeIdx;

        return this.jdbcTemplate.query(selectStoreImageUrlsQuery,
                (rs,rowNum) -> new String(
                        rs.getString("imageUrl")), selectStoreImageUrlsParams);
    }
}
