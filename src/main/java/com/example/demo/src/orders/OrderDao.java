package com.example.demo.src.orders;

import com.example.demo.src.orders.model.GetCartRes;
import com.example.demo.src.orders.model.PostOrderMenus;
import com.example.demo.src.orders.model.PostOrderReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OrderDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int insertOrder(PostOrderReq postOrderReq) {
        String insertOrderQuery = "INSERT INTO Orders (address, storeIdx, orderPrice, deliveryPrice, discountPrice, totalPrice, " +
                "storeRequests, CheckEchoProduct, deliveryRequests, userIdx, payType) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertOrderParams = new Object[] {postOrderReq.getAddress(), postOrderReq.getStoreIdx(), postOrderReq.getOrderPrice(),
                                    postOrderReq.getDeliveryPrice(), postOrderReq.getDiscountPrice(), postOrderReq.getTotalPrice(),
                                    postOrderReq.getStoreRequests(), postOrderReq.getCheckEchoProduct(), postOrderReq.getDeliveryRequests(),
                                    postOrderReq.getUserIdx(), postOrderReq.getPayType()};

        this.jdbcTemplate.update(insertOrderQuery, insertOrderParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int insertOrderMenu(PostOrderMenus orderMenus, int orderIdx) {
        String insertOrderMenuQuery = "INSERT INTO OrderMenu (orderIdx, menuName, menuDetail, count, totalPrice, menuIdx) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Object[] insertOrderMenuParams = new Object[] {orderIdx, orderMenus.getMenuName(), orderMenus.getMenuDetail(),
                orderMenus.getCount(), orderMenus.getTotalPrice(), orderMenus.getMenuIdx()};

        this.jdbcTemplate.update(insertOrderMenuQuery, insertOrderMenuParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public GetCartRes getCartUserInfo(int userIdx) {
        String query = "select case when Address.status = 'COMPANY' then '회사' " +
                "when Address.status = 'HOME' then '집' " +
                "when alias is null || length(Address.alias) = 0 then address " +
                "else alias end as 'mainAddress', " +
                "concat(Address.roadAddress, ' ',Address.detailAddress) as 'address', " +
                "User.payType " +
                "from User inner join Address on User.addressIdx = Address.idx " +
                "where User.idx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum)-> new GetCartRes(
                        rs.getString("mainAddress"),
                        rs.getString("address"),
                        rs.getString("payType")), userIdx);
    }
}
