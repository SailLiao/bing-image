package top.sailliao.bing.crawler;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.sailliao.bing.entity.Image;
import top.sailliao.bing.service.ImageService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抓取 bing 图片
 *
 * @author wemew
 */
@Component
public class BingIndexCrawler {

    private static final Logger logger = LoggerFactory.getLogger(BingIndexCrawler.class);

    private static final String url = "https://cn.bing.com/?FORM=Z9FD1";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    ImageService imageService;

    @Scheduled(cron = "0 0 1 * * *")
    public void processor() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("request {} error ", url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String html = response.body().string();
                    Document document = Jsoup.parse(html);
                    Elements elements = document.getElementsByClass("img_cont");
                    Element element = null;
                    if (elements != null && elements.size() > 0) {
                        element = elements.first();
                        String imageUlr = element.attr("style");
                        Pattern pattern = Pattern.compile("\\(([^}]*)\\)");
                        Matcher m = pattern.matcher(imageUlr);
                        if (m.find()) {
                            imageUlr = m.group();
                            imageUlr = imageUlr.replace("(", "").replace(")", "");
                            logger.info("image url {} ", imageUlr);
                        } else {
                            logger.error("未获取到图片");
                            return;
                        }

                        String description = "";
                        elements = document.getElementsByClass("title");
                        if (elements != null && elements.size() > 0) {
                            element = elements.first();
                            description = element.text();
                        }
                        logger.info("image description {} ", description);
                        String date = FORMAT.format(new Date());
                        Image image = new Image(imageUlr, description, date);
                        imageService.save(image);
                    } else {
                        logger.error("没有找到图片");
                    }
                }
            }
        });

    }

    public static void main(String[] args) {

        new BingIndexCrawler().processor();

    }

}
