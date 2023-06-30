package top.sailliao.bing.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.sailliao.bing.entity.Image;
import top.sailliao.bing.mapper.ImageMapper;
import top.sailliao.bing.service.ImageService;

import java.util.List;

/**
 * @author wemew
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    ImageMapper imageMapper;

    @Override
    public Image save(Image image) {
        imageMapper.insert(image);
        return image;
    }

    @Override
    public PageInfo<Image> page(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Image.class);
        example.setOrderByClause("date desc");
        List<Image> list = imageMapper.selectByExample(example);
        return new PageInfo(list);
    }
}
