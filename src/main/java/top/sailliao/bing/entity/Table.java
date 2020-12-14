package top.sailliao.bing.entity;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @author wemew
 */
@Data
public class Table {

    private List list;
    private int code;
    private int currentPage;
    private long totalNum;
    private int totalPage;

    public Table (PageInfo pageInfo) {
        this.list = pageInfo.getList();
        this.code = 200;
        this.totalNum = pageInfo.getTotal();
        this.totalPage = pageInfo.getPages();
    }

}
