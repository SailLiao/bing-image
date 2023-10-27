package top.sailliao.bing.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.sailliao.bing.entity.Image;
import top.sailliao.bing.entity.Table;
import top.sailliao.bing.service.ImageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lzf
 */
@Controller
public class IndexController {

    @Resource
    ImageService imageService;

    @RequestMapping("")
    public String index(Integer page, HttpServletRequest request) {
        page = page == null ? 0 : page;
        PageInfo<Image> pageInfo = imageService.page(page, 16);
        Table table = new Table(pageInfo);
        table.setCurrentPage(page);
        request.setAttribute("data", table);
        return "index";
    }

}
