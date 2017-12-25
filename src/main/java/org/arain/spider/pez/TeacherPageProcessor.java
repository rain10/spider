package org.arain.spider.pez;

import java.beans.IntrospectionException;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.arain.spider.util.ExcelBean;
import org.arain.spider.util.ExcelUtils;
import org.arain.spider.util.TimeUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TeacherPageProcessor implements PageProcessor{
	 //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000);
    private static int num = 0;
    private static int p =1;
    
    private static List<Teacher> list = new ArrayList<>();

    @Override
    public void process(Page page) {
    	 System.out.println(page.getUrl());
    	 //http://www.pxez.org/list/?id=56&siteid=1&page=2
    	if(page.getUrl().regex("http://www\\.pxez\\.org/list/\\?id=56&siteid=1&page=[0-9]+").match()) {
    		page.addTargetRequests(page.getHtml().xpath("//td[@style='text-align:center; padding:5px;']").links().all());
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
    		String p = page.getHtml().xpath("//div[@id='pagelist']/span/a/@href").get();
    		if(StringUtils.isNotBlank(p)) {
    			String string = p.subSequence(p.length()-1, p.length()).toString();
    			if(Integer.valueOf(string)>TeacherPageProcessor.p) {
    				TeacherPageProcessor.p=Integer.valueOf(string);
    				do_spider();
    			} 
    			if(Integer.valueOf(string)<TeacherPageProcessor.p) {
    				String p1 = page.getHtml().xpath("//div[@id='pagelist']/span[3]/a/@href").get();
    				if(StringUtils.isNotBlank(p1)) {
    	    			String string1 = p1.subSequence(p1.length()-1, p1.length()).toString();
    	    			if(Integer.valueOf(string1)>TeacherPageProcessor.p) {
    	    				TeacherPageProcessor.p=Integer.valueOf(string1);
    	    				do_spider();
    	    			} 
    	    		}
    			}
    		}
    	} else {
    		num++;
    		Teacher teacher = new Teacher();
    		String name = page.getHtml().xpath("//div[@id='ContentShow_title']/text()").get();
    		String desc = page.getHtml().xpath("//div[@id='ContentShow_Content']/p/text()").get();
    		String image = page.getHtml().xpath("//div[@id='ContentShow_Content']/p/img/@src").get();
    		teacher.setName(name);
    		teacher.setDesc(desc);
    		teacher.setImage(image);
    		
    		System.out.println(teacher.toString());
    		list.add(teacher);
    	}
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) throws Exception, IllegalAccessException, InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException {
        long startTime ,endTime;
        startTime = new Date().getTime();
        new TeacherPageProcessor().do_spider();
        endTime = new Date().getTime();
        System.out.println("一共爬到"+num+"个用户信息！用时为："+(endTime-startTime)/1000+"s");
        new TeacherPageProcessor().export();
    }
    
    public void do_spider () {
    	Spider.create(new TeacherPageProcessor()).addUrl("http://www.pxez.org/list/?id=56&siteid=1&page="+p).thread(5).run();
    }
    
    public void export() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException {
    	List<ExcelBean> ems = new ArrayList<>();
    	Map<Integer, List<org.arain.spider.util.ExcelBean>> map = new LinkedHashMap<>();
		ems.add(new ExcelBean("名字", "name", 0));
		ems.add(new ExcelBean("描述", "desc", 0));
		ems.add(new ExcelBean("图片", "image", 0));
		map.put(0, ems);
		XSSFWorkbook workbook = ExcelUtils.createExcelFile(Teacher.class, list, map, "郫县二中信息");
		FileOutputStream output;  
        try {  
        	output = new FileOutputStream("C:\\Users\\Arain\\Desktop\\"+TimeUtil.getNow(TimeUtil.FORMAT_INT)+".xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output); 
            bufferedOutPut.flush();  
            workbook.write(bufferedOutPut);  
            bufferedOutPut.close();  
            output.close();
            System.out.println("写文件成功！");
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
}

