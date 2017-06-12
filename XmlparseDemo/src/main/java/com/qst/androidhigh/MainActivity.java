package com.qst.androidhigh;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private TextView tv_info;
    private AssetManager am;
    private String preTag = null; // sax解析时记录正在解析的节点名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_info = (TextView) findViewById(R.id.tv_info);
        am = getAssets();
        //domParse();

        Long start = System.currentTimeMillis();
        pull();

        Long end = System.currentTimeMillis();
        Log.i("tag", "dom解析用時" + (end - start));

        Long start1 = System.currentTimeMillis();
        saxParse();
        Long end1 = System.currentTimeMillis();
        Log.i("tag", "sax解析用时" + (end1 - start1));
    }

    public void pull() {
        int a = 1;
        XmlPullParserFactory xpf = null;//构造工厂实例
        try {
            xpf = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xpf.newPullParser();//创建解析对象
            InputStream is = am.open("test.xml");
            parser.setInput(is, "UTF-8");
            int type = parser.getEventType();//此时返回０，也就是在START_DOCUMENT
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.i("tag","开始文档");
                        break;
                    case XmlPullParser.START_TAG:
                        Log.i("tag","开始元素");
                        if(parser.getName().equals("Student")){
                            tv_info.append("第" + a++ + "个学生：\n");
                            tv_info.append("School:" + parser.getAttributeValue(0) + "\n");
                        }else if(parser.getName().equals("Name")){
                            tv_info.append("Name:" + parser.nextText() + "\n");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.i("tag","结束元素");
                        break;
                }
                type = parser.next();//当前解析位置结束，指向下一个位置
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    int index = 1;
    private DefaultHandler defaultHandler = new DefaultHandler(){
        @Override
        public void startDocument() throws SAXException {

            super.startDocument();
            System.out.println("开始读取XML文档");
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            System.out.println("读取XML文档结束");
        }

        public void startElement (String uri, String localName, String qName, Attributes attributes){
            System.out.println("标签开始");
            if ("Student".equals(qName)) {
                tv_info.append("第" + index + "个学生：\n");
                tv_info.append("school:" + attributes.getValue("school") + "\n");
                index++;
            }
            preTag = qName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            System.out.println("标签结束");
            preTag = null;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (preTag != null) {
                String content = new String(ch, start, length);
                if (preTag.equals("Name")) {
                    tv_info.append("Name:" + content + "\n");
                } else if (preTag.equals("Num")) {
                    tv_info.append("Num:" + content + "\n");
                } else if (preTag.equals("Classes")) {
                    tv_info.append("Classes:" + content + "\n");
                } else if (preTag.equals("Address")) {
                    tv_info.append("Address:" + content + "\n\n");
                }
            }
        }
    };

    public void saxParse() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sax = spf.newSAXParser();
            InputStream istream = getAssets().open("test.xml");

            sax.parse(istream, defaultHandler);
            istream.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void domParse() {
        //得到dom解析器的工厂实例
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //从dom工厂中获得dom解析器
        try {
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //把要解析的xml文档读入dom解析器
            InputStream is = am.open("test.xml");
            Document document = dbBuilder.parse(is);
            //得到文档名称为Student的元素的节点列表
            NodeList nList = document.getElementsByTagName("Student");
            //遍历该集合，显示集合中的元素及其子元素的名字
            for (int i = 0;i<nList.getLength(); i++){
                Element node = (Element) nList.item(i);
                tv_info.append("第" +(i+1)+ "个学生：\n");
                tv_info.append("school:" + node.getAttribute("school") + "\n");
                tv_info.append("Name:" + node.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue() + "\n");
                tv_info.append("Num:" + node.getElementsByTagName("Num").item(0).getFirstChild().getNodeValue() + "\n");
                tv_info.append("Classes:" + node.getElementsByTagName("Classes").item(0).getFirstChild().getNodeValue() + "\n");
                tv_info.append("Address:" + node.getElementsByTagName("Address").item(0).getFirstChild().getNodeValue() + "\n");
            }
            //关闭输入流
            is.close();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
