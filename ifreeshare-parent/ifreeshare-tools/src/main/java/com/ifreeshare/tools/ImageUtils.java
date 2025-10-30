package com.ifreeshare.tools;

import net.coobird.thumbnailator.Thumbnails;

import java.io.*;

public class ImageUtils {

  public static void main(String[] args) throws IOException {
    //

      String filePath = "/Volumes/disk/柚木/【柚木写真】50套无水印高质量合集【46G】";


      File file = new File(filePath);
      File[] files = file.listFiles();

      for (File dir : files) {
         String dirPath =  dir.getAbsolutePath();
          File cachedir = new File(dirPath+"/chache");
          if(!cachedir.exists()){
              cachedir.mkdir();
          }

          if(!dir.isDirectory()){
              continue;
          }

          File[] childs = dir.listFiles();

          if(childs == null) continue;

          for ( File child : childs) {
              System.out.println(child.getName());
              String outputFilePath = dir.getAbsoluteFile()+"/chache/"+child.getName();

              File outputFile = new File(outputFilePath);
              if(outputFile.exists()){
                  outputFile.delete();
              }
              if(child.getName().endsWith("jpg")){
                  try{
                      Thumbnails.of(child)
                              .scale(1f) //图片大小（长宽）压缩比例 从0-1，1表示原图
                              .outputQuality(0.3f) //图片质量压缩比例 从0-1，越接近1质量越好
                              .toOutputStream(new FileOutputStream(outputFilePath));
                  }catch (Exception exception){
                      exception.printStackTrace();
                  }
              }

          }
      }




  }
}
