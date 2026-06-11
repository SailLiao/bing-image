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

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
