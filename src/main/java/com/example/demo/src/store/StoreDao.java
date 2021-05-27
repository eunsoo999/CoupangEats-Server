package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
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

    public List<GetStoreSmallBox> selectOnsaleStoresUptoTen(String lat, String lon) {
        String selectOnsaleStoresUptoTenQuery = "select onSaleStore.idx as 'storeIdx', onSaleStore.storeName, " +
                "(select StoreImage.imageUrl from StoreImage where StoreImage.storeIdx = onSaleStore.idx limit 1) as 'imageUrl', " +
                "(select if (count(*) = 0, null, concat(truncate(avg(rating), 1), ' (', count(*), ')')) " +
                "from Review " +
                "where Review.storeIdx = onSaleStore.idx and Review.status != 'N') as 'totalReview', " +
                "concat(if(truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " +
                "+sin(radians(?))*sin(radians(latitude)))),1) < 0.1, '0.1', " +
                "truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " +
                "+sin(radians(?))*sin(radians(latitude)))),1)), 'km') as 'distance', " +
                "concat(onSaleStore.discountPrice, '원 쿠폰') as 'coupon' " +
                "from (select distinct Store.idx, Store.storeName, Store.latitude, Store.longitude, Store.status, Coupon.discountPrice " +
                "from Store inner join Coupon on Store.idx = Coupon.storeIdx " +
                "where Store.status != 'N' and Coupon.status != 'N' and now() < Coupon.ExpirationDate " +
                "group by Store.idx) onSaleStore limit 10";

        Object[] selectOnsaleStoresParams = new Object[]{lat, lon, lat, lat, lon, lat};

        return this.jdbcTemplate.query(selectOnsaleStoresUptoTenQuery,
                (rs,rowNum) -> new GetStoreSmallBox(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getString("totalReview"),
                        rs.getString("distance"),
                        rs.getString("coupon")), selectOnsaleStoresParams);
    }

    public List<GetNewStoreBox> selectNewStoreBoxesUptoTen(String lat, String lon) {
        String selectNewStoreBoxesUptoTenQuery = "select Store.idx as 'storeIdx', Store.storeName, " +
                "(select StoreImage.imageUrl from StoreImage where StoreImage.storeIdx = Store.idx limit 1) as 'imageUrl', " +
                "(select if (count(*) = 0, null, concat(truncate(avg(rating), 1), ' (', count(*), ')')) " +
                "from Review " +
                "where Review.storeIdx = Store.idx and Review.status != 'N') as 'totalReview', " +
                "concat(if(truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " +
                "+sin(radians(?))*sin(radians(latitude)))),1) < 0.1, '0.1', " +
                "truncate((6371*acos(cos(radians(?))*cos(radians(latitude)) " +
                "*cos(radians(longitude)-radians(?)) " +
                "+sin(radians(?))*sin(radians(latitude)))),1)), 'km') as 'distance', " +
                "if ((select concat(Coupon.discountPrice, '원 쿠폰') " +
                "from Coupon " +
                "where Coupon.status != 'N' and now() < Coupon.ExpirationDate and Store.idx = Coupon.storeIdx limit 1) is not null, null, " +
                "case when Store.deliveryPrice = 0 then '무료배달'" +
                "else concat('배달비 ', FORMAT(Store.deliveryPrice , 0), '원') " +
                "end) as 'deliveryPrice', " +
                "(select concat(Coupon.discountPrice, '원 쿠폰') " +
                "from Coupon " +
                "where Coupon.status != 'N' and now() < Coupon.ExpirationDate and Store.idx = Coupon.storeIdx limit 1) as 'coupon' " +
                "from Store " +
                "where Store.status != 'N' and TIMESTAMPDIFF(WEEK, Store.createdAt, CURRENT_TIMESTAMP()) <= 2 " +
                "having distance < 4 order by Store.createdAt DESC " +
                "limit 10";

        Object[] selectNewStoreBoxesParams = new Object[]{lat, lon, lat, lat, lon, lat};

        return this.jdbcTemplate.query(selectNewStoreBoxesUptoTenQuery,
                (rs,rowNum) -> new GetNewStoreBox(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getString("totalReview"),
                        rs.getString("distance"),
                        rs.getString("coupon"),
                        rs.getString("deliveryPrice") ), selectNewStoreBoxesParams);
    }

    public List<GetStoreCategoryRes> selectStoreCategories() {
        String selectStoreCategoriesQuery = "select categoryName, imageUrl from StoreCategory";

        return this.jdbcTemplate.query(selectStoreCategoriesQuery,
                (rs,rowNum) -> new GetStoreCategoryRes(
                        rs.getString("categoryName"),
                        rs.getString("imageUrl")));
    }
}
