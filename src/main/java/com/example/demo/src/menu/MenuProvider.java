package com.example.demo.src.menu;

import com.example.demo.config.BaseException;
import com.example.demo.src.menu.model.GetMenuDetailRes;
import com.example.demo.src.menu.model.GetMenuOptionCategorys;
import com.example.demo.src.menu.model.GetMenuOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.MENU_NOT_IN_STORES;

@Service
public class MenuProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MenuDao menuDao;

    @Autowired
    public MenuProvider(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    public GetMenuDetailRes selectMenu(int storeIdx, int menuIdx) throws BaseException {
        // 해당 가게의 메뉴인지 확인
        if (menuDao.checkMenuInStore(storeIdx, menuIdx) == 0 ) {
            throw new BaseException(MENU_NOT_IN_STORES);
        }

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
        if(!optionCategorys.isEmpty()) {
            for (GetMenuOptionCategorys optionCategory : optionCategorys) {
                List<GetMenuOptions> Options = menuDao.selectOptionsByMenuIdxAndCategoryName(menuIdx, optionCategory.getOptionCategoryName());
                if(!Options.isEmpty()) {
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
    }
}
