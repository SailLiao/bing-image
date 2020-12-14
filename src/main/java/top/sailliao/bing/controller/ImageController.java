package top.sailliao.bing.controller;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sailliao.bing.service.ImageService;

/**
 * @author wemew
 */
@RestController
@RequestMapping("image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    ImageService imageService;

    @RequestMapping("page")
    public PageInfo page(Integer pageNum, Integer pageSize) {
        return imageService.page(pageNum, pageSize);
    }

}
