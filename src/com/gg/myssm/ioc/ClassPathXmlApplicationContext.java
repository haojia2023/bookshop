package com.gg.myssm.ioc;

import com.gg.myssm.util.StringUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class ClassPathXmlApplicationContext implements BeanFactory{
    private HashMap<String,Object> beanMap = new HashMap();
    String path = "applicationContext.xml";

    public ClassPathXmlApplicationContext(){
        this("applicationContext.xml");
    }

    public ClassPathXmlApplicationContext(String path){
        if(!StringUtil.isNoNull(path)){
            throw new RuntimeException("ioc容器配置文件没有指定...");
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("applicationContext.xml");
        try {
            NodeList beans = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getElementsByTagName("bean");
            //存入bean
            for (int i = 0; i  < beans.getLength(); i++) {
                Node beanNode = beans.item(i);
                if(beanNode.getNodeType() == Node.ELEMENT_NODE){
                    Element beanElement = (Element) beanNode;
                    Object beanObj = Class.forName(beanElement.getAttribute("class")).newInstance();
                    beanMap.put(beanElement.getAttribute("id"),beanObj);
                }
            }
            //依赖注入
            for (int i = 0; i < beans.getLength(); i++) {
                Node beanNode = beans.item(i);
                if(beanNode.getNodeType() == Node.ELEMENT_NODE){
                    Element beanElement = (Element) beanNode;
                    NodeList childNodes = beanElement.getChildNodes();
                    String beanId = beanElement.getAttribute("id");
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node beanChildNode = childNodes.item(j);
                        if(beanChildNode.getNodeType() == Node.ELEMENT_NODE && "property".equals(beanChildNode.getNodeName())){
                            Element propertyElement = (Element) beanChildNode;
                            String propertyName = propertyElement.getAttribute("name");
                            String propertyRef = propertyElement.getAttribute("ref");
                            Object beanObj = beanMap.get(beanId);
                            Field propertyField = beanObj.getClass().getDeclaredField(propertyName);
                            propertyField.setAccessible(true);
                            propertyField.set(beanObj,beanMap.get(propertyRef));

                        }
                    }

                }
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
