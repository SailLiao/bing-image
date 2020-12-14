package top.sailliao.bing.controller;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.sailliao.bing.entity.Table;
import top.sailliao.bing.service.ImageService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzf
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    ImageService imageService;

    @RequestMapping("")
    public String index(Integer page, HttpServletRequest request) {
        page = page == null ? 0 : page;
        PageInfo pageInfo = imageService.page(page, 16);
        Table table = new Table(pageInfo);
        table.setCurrentPage(page);
        request.setAttribute("data", table);
        return "index";
    }

}
