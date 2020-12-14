package top.sailliao.bing.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import top.sailliao.bing.entity.Image;

/**
 * @author wemew
 */
@Repository
public interface ImageMapper extends Mapper<Image> {

    @Select("select * from image where id = #{id}")
    public Image getImageById(@Param("id") long id);
}
