package com.rdweiba.main.com.multimedia.main.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class MohoXml {
	private Document document;
	private String filename;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	
	public MohoXml(String file) throws ParserConfigurationException{
		filename = file;
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
	}
	
	public ArrayList<String> getItems(String str){
		ArrayList<String> al_file = new ArrayList<String>();
		ArrayList<String> al_path = new ArrayList<String>();
		try {
			//read file
			Document doc = db.parse(filename);
			NodeList nl = doc.getElementsByTagName("item");
			int len = nl.getLength();
			if (len == 0){
				return null;
			}else {
				for (int i = 0 ; i < len ; i++){
					Element element = (Element) nl.item(i);
					String s_file = element.getAttribute("file");
					String s_path = element.getAttribute("path");
					al_file.add(s_file);
					al_path.add(s_path);
				}
			}
			
			if (str == "file"){
				return al_file;
			}else if (str == "path"){
				return al_path;
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
		
	}
	
	public void createNewXml(File f){
		Document writedoc = db.newDocument();
		Element rootElement = writedoc.createElement("playlist");
		writedoc.appendChild(rootElement);
	}
	
	
	public void createFile(String str){
		File f = new File("/sdcard/moho");
		if (!f.exists() && f.isDirectory()){
			f.mkdir();
		}
		File xmlfile = new File("/sdcard/moho"+str);
		if (!xmlfile.exists()){
			try {
				xmlfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeXml(File f,String filename,String filepath){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer  = new StringWriter();
		try{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
