package org.arain.spider.zhihu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class ZhiHuUserPageProcessor implements PageProcessor{
	 //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000);
    //用户数量
    private static int num = 0;
    //搜索关键词
    private static String keyword = "java";
    //数据库持久化对象，用于将用户信息存入数据库


    /**
     * process 方法是webmagic爬虫的核心<br>
     * 编写抽取【待爬取目标链接】的逻辑代码在html中。
     */
    @Override
    public void process(Page page) {
    	System.out.println("==================="+page.getUrl());
        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("https://www\\.zhihu\\.com/search\\?type=people&q=[\\s\\S]+").match()){
        	BufferedWriter output = null;
            try {
                File file = new File("C:\\Users\\Arain\\Desktop\\example.txt");
                output = new BufferedWriter(new FileWriter(file));
                output.write(page.getHtml()+"");
            } catch ( IOException e ) {
                e.printStackTrace();
            } finally {
                if ( output != null )
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            }
        	
            page.addTargetRequests(page.getHtml().xpath("//div[@class='List-item']/div/div/div/span/div/div/a[@class='UserLink-link']").links().all());
            System.out.println("---------------------"+page.getTargetRequests());
        }
        //2. 如果是用户详细页面
        else{
            num++;//用户数++
            /*实例化ZhihuUser，方便持久化存储。*/
            ZhihuUser user = new ZhihuUser();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String name = page.getHtml().xpath("//div[@class='ProfileHeader-content']/div/h1/span[@class='ProfileHeader-name']/text()").get();
         
            user.setName(name);
//            System.out.println("num:"+num +" " + user.toString());//输出对象
        }
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
        long startTime ,endTime;
        System.out.println("========知乎用户信息小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        //入口为：【https://www.zhihu.com/search?type=people&q=xxx 】，其中xxx 是搜索关键词
        Spider.create(new ZhiHuUserPageProcessor()).addUrl("https://www.zhihu.com/search?type=people&q="+keyword).thread(5).run();
        endTime = new Date().getTime();
        System.out.println("========知乎用户信息小爬虫【结束】喽！=========");
        System.out.println("一共爬到"+num+"个用户信息！用时为："+(endTime-startTime)/1000+"s");
    }
}

