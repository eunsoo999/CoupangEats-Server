package com.example.demo.src.menu;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.menu.model.GetMenuDetailRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
public class MenuController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MenuProvider menuProvider;

    public MenuController(MenuProvider menuProvider) {
        this.menuProvider = menuProvider;
    }

    /**
     * 24. 메뉴 상세 조회 API
     * [GET] /stores/:storesIdx/menus/:menuIdx
     * @return BaseResponse<GetMenuDetailRes>
     */
    @ResponseBody
    @GetMapping("/stores/{storeIdx}/menus/{menuIdx}")
    public BaseResponse<GetMenuDetailRes> getMenu(@PathVariable int storeIdx, @PathVariable int menuIdx) {
        try {
            GetMenuDetailRes menuDetail = menuProvider.selectMenu(storeIdx, menuIdx);
            return new BaseResponse<>(menuDetail);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
