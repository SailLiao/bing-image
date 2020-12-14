package top.sailliao.bing.service;

import com.github.pagehelper.PageInfo;
import top.sailliao.bing.entity.Image;

/**
 * @author wemew
 */
public interface ImageService {

    Image save(Image image);

    PageInfo page(Integer pageNum, Integer pageSize);
}
