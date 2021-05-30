package com.example.demo.src.menu;

import com.example.demo.src.menu.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                "where BestMenu.status != 'N' and Menu.status != 'N' and BestMenu.storeIdx = ?";

        return this.jdbcTemplate.query(selectBestMenusQuery,
                (rs,rowNum) -> new GetMenus(
                        rs.getInt("menuIdx"),
                        rs.getString("menuName"),
                        rs.getString("imageUrl"),
                        rs.getInt("price"),
                        rs.getString("introduction")), storeIdx);
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

    public GetMenuDetailRes selectMenu(int menuIdx) {
        String selectMenuQuery = "select menuName, price, introduction from Menu where Menu.idx = ? and Menu.status != 'N'";
        return this.jdbcTemplate.queryForObject(selectMenuQuery,
                (rs,rowNum) -> new GetMenuDetailRes(
                        rs.getString("menuName"),
                        rs.getString("introduction"),
                        rs.getInt("price")), menuIdx);
    }

    public List<String> selectMenuImageUrls(int menuIdx) {
        String selectMenuImageUrlsQuery = "select imageUrl from MenuImage where menuIdx = ?";

        return this.jdbcTemplate.query(selectMenuImageUrlsQuery,
                (rs,rowNum) -> new String(
                        rs.getString("imageUrl")), menuIdx);
    }

    public List<GetMenuOptionCategorys> selectMenuOptionCategorys(int menuIdx) {
        String selectMenuImageUrlsQuery = "select title as 'optionCategoryName', requiredChoiceFlag, numberOfChoices " +
                "from MenuOptionCategory " +
                "where MenuOptionCategory.menuIdx = ? and MenuOptionCategory.status != 'N'";

        return this.jdbcTemplate.query(selectMenuImageUrlsQuery,
                (rs,rowNum) -> new GetMenuOptionCategorys(
                        rs.getString("optionCategoryName"),
                        rs.getString("requiredChoiceFlag"),
                        rs.getInt("numberOfChoices")), menuIdx);
    }

    public List<GetMenuOptions> selectOptionsByMenuIdxAndCategoryName(int menuIdx, String optionCategoryName) {
        String selectOptionsQuery = "select MenuOption.title as 'optionName', MenuOption.extraPrice " +
                "from MenuOption inner join MenuOptionCategory on MenuOption.optionCategoryIdx = MenuOptionCategory.idx " +
                "where MenuOptionCategory.title = ? and MenuOptionCategory.menuIdx = ? and MenuOption.status != 'N'";

        return this.jdbcTemplate.query(selectOptionsQuery,
                (rs,rowNum) -> new GetMenuOptions(
                        rs.getString("optionName"),
                        rs.getInt("extraPrice")), optionCategoryName, menuIdx);
    }

    public int checkMenuInStore(int storeIdx, int menuIdx) {
        String checkMenuInStoreQuery = "select exists(select idx from Menu where idx = ? and Menu.storeIdx = ? and Menu.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkMenuInStoreQuery, int.class, menuIdx, storeIdx);
    }

    public int checkMenu(int menuIdx) {
        String checkMenuInStoreQuery = "select exists(select idx from Menu where idx = ? and Menu.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkMenuInStoreQuery, int.class, menuIdx);
    }
}
