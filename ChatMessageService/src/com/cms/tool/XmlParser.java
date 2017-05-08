package com.cms.tool;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import com.cms.bean.User;
import com.cms.client.ServiceWindows;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Created by gavin on 2017/5/4.
 */
public class XmlParser {
    /**
     * 解析xml文件
     * 在这里将登录用户的一部分数据保存在xml文件当中，相当于一个数据库
     * 根据ID查询登录用户
     */
    public static User queryUserById(long userId){
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new File("loginUser.xml"));
            Element root=doc.getRootElement();
            Iterator<Element> iter=root.elementIterator("user");
            while(iter.hasNext()){
                Element item =(Element)iter.next();
                String id = item.attribute("id").getText();
                if(Integer.parseInt(id) == userId){
                    User user = new User();
                    user.setId(userId);
                    user.setName(item.element("name").getText());
                    user.setImg(item.element("image").getText());
                    return user;
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 保存登录用户信息
     * @param name
     * @param input
     */
    public static String saveUserInfo(String name,
                                      DataInputStream input) {
        SAXReader read=new SAXReader();
        FileOutputStream output = null;
        XMLWriter xmlwriter = null;
        String uniqueId="";
        try {
            String filePath = "loginUser.xml";
            Document doc=read.read(new File(filePath));
            Element root=doc.getRootElement();
            Element userElement=root.addElement("user");
            uniqueId = String.valueOf(getUniqueNum());
            userElement.addAttribute("id", uniqueId);
            userElement.addElement("name").setText(name);
            String fileName = System.currentTimeMillis()+".png";
            userElement.addElement("image").setText(fileName);
            File saveFile = new File("image/"+fileName);
            output = new FileOutputStream(saveFile);
            int readCount = 0;
            int totalCount=input.readInt();
            byte buffers[] = new byte[totalCount];
            int len = 0;
            while(readCount < totalCount){
                len = input.read(buffers, 0, totalCount - readCount);
                readCount+= len;
                output.write(buffers, 0, len);
            }
            xmlwriter=new XMLWriter(new FileOutputStream(filePath),OutputFormat.createPrettyPrint());
            xmlwriter.write(doc);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (xmlwriter != null)
                    xmlwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uniqueId;
    }

    /**
     * 生成当前注册用户的唯一标识
     */
    public static long getUniqueNum(){
        SAXReader reader = new SAXReader();
        long firstNum = 1000;
        long newNum = 0;
        try {
            Document doc = reader.read(new File("loginUser.xml"));
            Element root=doc.getRootElement();
            Iterator<Element> iter=root.elementIterator("user");
            while(iter.hasNext()){
                Element item =(Element)iter.next();
                long temp = Long.parseLong(item.attribute("id").getText());
                if(newNum < temp){
                    newNum = temp;
                }
            }
            if(newNum!= 0){
                newNum = newNum+1;
            }else{
                newNum = firstNum;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return newNum;
    }
    /**
     * 生成消息的唯一标识
     */
    public static String getUniqueMsgId() {
        Random random = new Random();
        String uniqueNum = System.currentTimeMillis() + "" + random.nextInt(1000);
        return uniqueNum;
    }
}
