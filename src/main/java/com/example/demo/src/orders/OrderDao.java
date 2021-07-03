package com.example.demo.src.orders;

import com.example.demo.src.orders.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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
                "concat(Address.roadAddress, ' ', if(Address.detailAddress is not null, Address.detailAddress, '')) as 'address', " +
                "User.payType " +
                "from User inner join Address on User.addressIdx = Address.idx " +
                "where User.idx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum)-> new GetCartRes(
                        rs.getString("mainAddress"),
                        rs.getString("address"),
                        rs.getString("payType")), userIdx);
    }

    public int checkOrderIdx(int orderIdx) {
        String checkOrderIdxQuery = "select exists(select idx from Orders where idx = ? and status != 'N')";
        int checkOrderIdxParams = orderIdx;

        return this.jdbcTemplate.queryForObject(checkOrderIdxQuery, int.class, checkOrderIdxParams);
    }

    public int checkOrderInStore(int orderIdx, int storeIdx) {
        String checkOrderIdxQuery = "select exists(select idx from Orders where idx = ? and storeIdx = ? and status != 'N')";

        return this.jdbcTemplate.queryForObject(checkOrderIdxQuery, int.class, orderIdx, storeIdx);
    }

    // 해당 주문내역에 주문메뉴가 있는지 검증
    public int checkOrderMenuInOrder(int orderIdx, int orderMenuIdx) {
        String checkOrderMenuInOrder = "select exists(select idx from OrderMenu " +
                "where OrderMenu.status != 'N' and OrderMenu.orderIdx = ? and OrderMenu.idx = ?)";

        return this.jdbcTemplate.queryForObject(checkOrderMenuInOrder, int.class, orderIdx, orderMenuIdx);
    }

    // 유저가 주문한 주문번호가 맞는지 검증
    public int checkOrderByUserIdx(Integer orderIdx, Integer userIdx) {
        String checkOrderByUserIdxQuery = "select exists(select idx from Orders " +
                "where Orders.status != 'N' and Orders.idx = ? and Orders.userIdx = ?)";
        return this.jdbcTemplate.queryForObject(checkOrderByUserIdxQuery, int.class, orderIdx, userIdx);
    }

    public List<GetPastOrder> selectPastOrders(int userIdx) {
        String selectPastOrdersQuery = "select Orders.idx as 'orderIdx', Store.idx as 'storeIdx', Store.storeName, " +
                "FirstStoreImage.imageUrl, Orders.createdAt as 'orderDate', " +
                "case Orders.status when 'FINISH' then '배달 완료' " +
                "when 'CANCEL' then '주문취소됨' end 'status', " +
                "FORMAT(Orders.orderPrice, 0) as 'orderPrice', " +
                "FORMAT(Orders.deliveryPrice, 0) as 'deliveryPrice', " +
                "FORMAT(Orders.discountPrice, 0) as 'discountPrice', " +
                "FORMAT(Orders.totalPrice , 0) as 'totalPrice', " +
                "Orders.payType, " +
                "Review.idx as 'reviewIdx', Review.rating " +
                "from Orders join Store on Orders.storeIdx = Store.idx " +
                "join (select storeIdx, min(idx) as 'firstReviewImage', imageUrl from StoreImage group by storeIdx) as FirstStoreImage on Store.idx = FirstStoreImage.storeIdx " +
                "left join (select idx, orderIdx, rating from Review where status = 'Y') Review on Review.orderIdx = Orders.idx " +
                "where Orders.userIdx = ? and (Orders.status = 'CANCEL' or Orders.status = 'FINISH') " +
                "order by Orders.createdAt desc";

        return this.jdbcTemplate.query(selectPastOrdersQuery,
                (rs,rowNum)-> new GetPastOrder(
                        rs.getInt("orderIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("imageUrl"),
                        rs.getString("orderDate"),
                        rs.getString("status"),
                        rs.getString("orderPrice"),
                        rs.getString("deliveryPrice"),
                        rs.getString("discountPrice"),
                        rs.getString("totalPrice"),
                        rs.getString("payType"),
                        rs.getObject("reviewIdx") != null ? rs.getInt("reviewIdx") : null,
                        rs.getObject("rating") != null ? rs.getInt("rating") : null), userIdx);

        // todo null
    }

    public List<GetPreparingOrder> selectPreparingOrders(int userIdx) {
        String selectPreparingOrders = "select Orders.idx as 'orderIdx', " +
                "Store.idx as 'storeIdx', " +
                "Store.storeName, " +
                "FirstStoreImage.imageUrl, " +
                "Orders.createdAt as 'orderDate', " +
                "case Orders.status when 'WAITING' then '준비중' " +
                "when 'DELIVERING' then '배달중' end 'status', " +
                "FORMAT(Orders.orderPrice, 0) as 'orderPrice', " +
                "FORMAT(Orders.deliveryPrice, 0) as 'deliveryPrice', " +
                "FORMAT(Orders.discountPrice, 0) as 'discountPrice', " +
                "FORMAT(Orders.totalPrice , 0) as 'totalPrice', " +
                "Orders.payType " +
                "from Orders join Store on Orders.storeIdx = Store.idx " +
                "join (select storeIdx, min(idx) as 'firstReviewImage', imageUrl from StoreImage group by storeIdx) as FirstStoreImage on Store.idx = FirstStoreImage.storeIdx " +
                "where Orders.userIdx = ? and (Orders.status = 'WAITING' or Orders.status = 'DELIVERING') " +
                "order by Orders.createdAt desc";

        return this.jdbcTemplate.query(selectPreparingOrders,
                (rs,rowNum)-> new GetPreparingOrder(
                        rs.getInt("orderIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("imageUrl"),
                        rs.getString("orderDate"),
                        rs.getString("status"),
                        rs.getString("orderPrice"),
                        rs.getString("deliveryPrice"),
                        rs.getString("discountPrice"),
                        rs.getString("totalPrice"),
                        rs.getString("payType")), userIdx);
    }

    public List<GetOrderMenuReview> selectOrderMenus(int orderIdx) {
        String selectOrderMenuQuery = "select OrderMenu.count, OrderMenu.menuName, OrderMenu.menuDetail, " +
                "FORMAT(OrderMenu.totalPrice, 0) as 'menuPrice', MenuReviewInfo.menuLiked " +
                "from OrderMenu left join " +
                "(select Review.orderIdx, MenuReview.idx as menuReviewIdx, MenuReview.orderMenuIdx, MenuReview.reviewIdx, MenuReview.menuLiked " +
                "from Review left join MenuReview on Review.idx = MenuReview.reviewIdx " +
                "where  MenuReview.status != 'N' and Review.status != 'N') " +
                "MenuReviewInfo on OrderMenu.idx = MenuReviewInfo.orderMenuIdx and MenuReviewInfo.orderIdx = OrderMenu.orderIdx " +
                "where OrderMenu.orderIdx = ?";

        return this.jdbcTemplate.query(selectOrderMenuQuery,
                (rs,rowNum)-> new GetOrderMenuReview(
                        rs.getInt("count"),
                        rs.getString("menuName"),
                        rs.getString("menuDetail"),
                        rs.getString("menuPrice"),
                        rs.getString("menuLiked")), orderIdx);
    }

    public GetOrderRes selectOrderDetail(int orderIdx) {
        String query = "select Store.idx as 'storeIdx', Store.storeName " +
                "from Orders join Store on Orders.storeIdx = Store.idx " +
                "where Orders.idx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum)-> new GetOrderRes(
                        rs.getInt("storeIdx"),
                        rs.getString("storeName")), orderIdx);

    }

    public List<GetOrderMenuRes> selectOrderMenusIdxAndNameAndDetail(int orderIdx) {
        String query = "select idx, menuName, menuDetail from OrderMenu where OrderMenu.orderIdx = ?";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new GetOrderMenuRes(
                        rs.getInt("idx"),
                        rs.getString("menuName"),
                        rs.getString("menuDetail")), orderIdx);
    }
}
