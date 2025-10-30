package com.demo.t66y;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class T66y {

    public static void main(String[] args) {


        Document document = null;
        try {
            document = Jsoup.connect("http://t66y.com/htm_data/2204/8/5043686.html").get();

            Element conttpc = document.getElementById("conttpc");

            Elements elements = conttpc.getElementsByTag("img");

            int i = 10000;
            for (Element element: elements
                 ) {
              String url = element.attr("ess-data");
              if(!StringUtil.isBlank(url)){
                  System.out.println(url);
                  String mineType = HttpUtil.getMimeType(url);
                  System.out.println(mineType);
                  String fileName = i+"";
                  if(mineType.equals("image/jpeg")){
                      fileName = fileName+".jpg";
                  }else{
                      fileName = fileName+".jpg";
                  }
                  long size = HttpUtil.downloadFile(url, FileUtil.file("/Users/fangyuan/Downloads/cache/"+fileName));
                  System.out.println("Download size: " + size);
                  i--;
              }
            }




//            System.out.println(conttpc);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
