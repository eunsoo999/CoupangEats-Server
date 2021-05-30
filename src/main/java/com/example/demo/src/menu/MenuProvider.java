package com.example.demo.src.menu;

import com.example.demo.config.BaseException;
import com.example.demo.src.menu.model.GetMenuDetailRes;
import com.example.demo.src.menu.model.GetMenuOptionCategorys;
import com.example.demo.src.menu.model.GetMenuOptions;
import com.example.demo.src.store.StoreDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class MenuProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MenuDao menuDao;
    private final StoreDao storeDao;

    @Autowired
    public MenuProvider(MenuDao menuDao, StoreDao storeDao) {
        this.menuDao = menuDao;
        this.storeDao = storeDao;
    }

    public GetMenuDetailRes selectMenu(int storeIdx, int menuIdx) throws BaseException {
        if(storeDao.checkStore(storeIdx) == 0) { // 가게 존재 확인
            throw new BaseException(STORES_NOT_FOUND);
        } else if(menuDao.checkMenu(menuIdx) == 0) { // 메뉴 존재 확인
            throw new BaseException(MENUS_NOT_FOUND);
        } else if (menuDao.checkMenuInStore(storeIdx, menuIdx) == 0 ) { // 해당 가게의 메뉴인지 확인
            throw new BaseException(MENU_NOT_IN_STORES);
        }

        try {
            // 메뉴 정보
            GetMenuDetailRes menuDetail = menuDao.selectMenu(menuIdx);

            // 메뉴 이미지
            List<String> menuImages = menuDao.selectMenuImageUrls(menuIdx);
            if (!menuImages.isEmpty()) {
                menuDetail.setImageUrls(menuImages);
            } else {
                menuDetail.setImageUrls(null);
            }

            // 메뉴 선택 옵션 카테고리
            List<GetMenuOptionCategorys> optionCategorys = menuDao.selectMenuOptionCategorys(menuIdx);

            // 메뉴 선택 옵션
            if (!optionCategorys.isEmpty()) {
                for (GetMenuOptionCategorys optionCategory : optionCategorys) {
                    List<GetMenuOptions> Options = menuDao.selectOptionsByMenuIdxAndCategoryName(menuIdx, optionCategory.getOptionCategoryName());
                    if (!Options.isEmpty()) {
                        optionCategory.setOptions(Options);
                    } else {
                        optionCategory.setOptions(null);
                    }
                }
                menuDetail.setMenuOptions(optionCategorys);
            } else {
                menuDetail.setMenuOptions(null);
            }
            return menuDetail;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
