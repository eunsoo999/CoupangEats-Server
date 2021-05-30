package com.example.demo.src.coupon;

import com.example.demo.src.coupon.model.GetStoreCoupon;
import com.example.demo.src.menu.model.GetMenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CouponDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkCouponUser(int couponIdx, Integer userIdx) {
        String checkCouponByStoreQuery = "select exists(select CouponUser.idx " +
                "from Coupon inner join CouponUser on Coupon.idx = CouponUser.couponIdx " +
                "where CouponUser.userIdx = ? and Coupon.idx = ? and CouponUser.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, userIdx, couponIdx);
    }

    public int checkCouponByStoreIdx(int storeIdx) {
        String checkCouponByStoreQuery = "select exists(select Coupon.idx " +
                "from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.ExpirationDate > now() and Coupon.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, storeIdx);
    }

    public GetStoreCoupon selectStoreCoupon(int storeIdx) {
        String selectStoreCoupon = "select idx as 'couponIdx', discountPrice from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.status != 'N' and Coupon.ExpirationDate > now()";

        return this.jdbcTemplate.queryForObject(selectStoreCoupon,
                (rs,rowNum) -> new GetStoreCoupon(
                        rs.getInt("couponIdx"),
                        rs.getInt("discountPrice")), storeIdx);
    }
}
