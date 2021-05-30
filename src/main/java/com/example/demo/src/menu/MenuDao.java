package com.example.demo.src.menu;


import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.menu.model.GetMenuByCategory;
import com.example.demo.src.menu.model.GetMenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.awt.*;
import java.util.List;

@Repository
public class MenuDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetMenus> selectMenusBycategoryName(String menuCategoryName) {
        String selectMenuQuery = "select Menu.idx as 'menuIdx', Menu.menuName, " +
                "Menu.price, Menu.introduction, FirstImage.imageUrl " +
                "from Menu inner join MenuCategory on Menu.menuCategoryIdx = MenuCategory.idx " +
                "left join (select menuIdx, min(idx), imageUrl from MenuImage " +
                "group by menuIdx) as FirstImage on Menu.idx = FirstImage.menuIdx " +
                "where menuCategoryName = ? and Menu.status != 'N'";

        return this.jdbcTemplate.query(selectMenuQuery,
                (rs,rowNum) -> new GetMenus(
                        rs.getInt("menuIdx"),
                        rs.getString("menuName"),
                        rs.getString("imageUrl"),
                        rs.getInt("price"),
                        rs.getString("introduction")), menuCategoryName);
    }

    public List<GetMenus> selectBestMenusByStoreIdx(int storeIdx) {
        String selectBestMenusQuery = "select Menu.idx as 'menuIdx', Menu.menuName, Menu.price, Menu.introduction, imageUrl " +
                "from BestMenu inner join Menu on BestMenu.menuIdx = Menu.idx left join (select menuIdx, min(idx), imageUrl from MenuImage " +
                "group by menuIdx) as FirstImage on Menu.idx = FirstImage.menuIdx " +
                "where BestMenu.status != 'N' and BestMenu.storeIdx = ?";

        return this.jdbcTemplate.query(selectBestMenusQuery,
                (rs,rowNum) -> new GetMenus(
                        rs.getInt("menuIdx"),
                        rs.getString("menuName"),
                        rs.getString("imageUrl"),
                        rs.getInt("price"),
                        rs.getString("introduction")), storeIdx);
    }

    public List<Integer> selectMenuCategoriesIdx(int storeIdx) {
        String selectMenuCategoriesIdxQuery = "select distinct menuCategoryIdx " +
                "from Menu where Menu.storeIdx = ? and Menu.status != 'N' " +
                "order by menuCategoryIdx ";

        return this.jdbcTemplate.query(selectMenuCategoriesIdxQuery,
                (rs,rowNum) -> new Integer(rs.getInt("menuCategoryIdx")), storeIdx);
    }

    public List<GetMenuByCategory> selectStoreCategories(int storeIdx) {
        String selectStoreCategoriesQuery = "select menuCategoryName, introduction " +
                "from MenuCategory " +
                "where MenuCategory.status != 'N' and MenuCategory.storeIdx = ?";

        return this.jdbcTemplate.query(selectStoreCategoriesQuery,
                (rs,rowNum) -> new GetMenuByCategory(
                        rs.getString("menuCategoryName"),
                        rs.getString("introduction")), storeIdx);
    }
}
