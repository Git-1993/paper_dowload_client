package com.example.controller;

import com.example.oss.OssUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class DowloadPaperController {
    String finalUrl = "";
    @RequestMapping(value = "/api/dowload")
    public String dowloadPaper(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //StringBuffer url = request.getRequestURL();
        String url="http://192.168.1.142:8888/ws/dowload/192.168.1.142:8989/demo/index.html";
        String objectName = get(url);
        OssUtil.testDowload(objectName,"D:\\ww2.doc");
        File file = new File("D:\\ww2.doc");
        //响应文件下载
        response.setHeader("Content-Disposition", "attachment; filename=\"" +file.getName());
        response.setContentType("application/msword;charset=UTF-8");
        InputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();

        // 写文件
        int b;
        //byte[] bytes = new byte[1024];
        while ((b = in.read()) != -1) {
            out.write(b);
        }

        in.close();
        out.close();
        file.delete();
        return objectName;
    }

    /**
     * HttpClien 的客户端访问
     */
    private void httpClientVisit() {

        String clientResponse = "";
        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("");

            //不是表单数据提交，这边使用 StringEntity 即可
            //UrlEncodedFormEntity等都是 HttpEntity 接口的实现类
            StringEntity entity = new StringEntity("", "UTF-8");//编码
            entity.setContentType("text/xml");
            request.setEntity(entity);
            // 发送请求
            HttpResponse response = client.execute(request);

            org.apache.http.HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                // EntityUtils.toString 如果不指定编码，EntityUtils默认会使用ISO_8859_1进行编码
                clientResponse = EntityUtils.toString(httpEntity, "UTF-8");// 记得设置编码或者如下
                // clientResponse = new String(EntityUtils.toString(httpEntity).getBytes("ISO_8859_1"), "UTF-8");
            }

            if (clientResponse == null || "".equals(clientResponse)) {
                System.err.println("clientResponse is null or empty.");

            }

            System.out.println(clientResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送 get请求
     */
    public String get(String finalUrl) {
        String result = "";
        try {
            HttpClient client = new DefaultHttpClient();
            // 创建httpget.
            HttpGet httpget = new HttpGet(finalUrl);
            System.out.println("executing request " + httpget.getURI());
            // 执行get请求.
            HttpResponse response = client.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                System.out.println("--------------------------------------");
                // 打印响应状态
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    // 打印响应内容长度
                    System.out.println("Response content length: " + entity.getContentLength());
                    // 打印响应内容
                    result = EntityUtils.toString(entity);
                    System.out.println("Response content: " + result);
                }
                System.out.println("------------------------------------");
            } finally {
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return result;
    }


}
