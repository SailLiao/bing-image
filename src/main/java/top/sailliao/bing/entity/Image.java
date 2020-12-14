package top.sailliao.bing.entity;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Table(name = "image")
public class Image {

    @Id
    private int id;
    private String description;
    private String date;
    private String url;

    public Image() {

    }

    public Image(String url, String description, String date) {
        this.url = url;
        this.description = description;
        this.date = date;
    }

}
