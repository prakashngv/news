package com.kisszo.news.netty.controller;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





public class AnnotatedPathManager {

	Logger log = LogManager.getLogger(AnnotatedPathManager.class);
	
	private static AnnotatedPathManager annotatedPathManager = null;
	
	private Map<String, PathImplementor> paths = new HashMap<String, PathImplementor>();
	
	private  AnnotatedPathManager() {
		try {
			System.out.println("Initializing Path anotation");
			init();
		} catch (IOException e) {
			log.fatal("Error while loading the path details", e);
		}
	}
	
	
	public static AnnotatedPathManager getInstance(){
		if(annotatedPathManager == null){
			synchronized (AnnotatedPathManager.class) {
				if(annotatedPathManager == null){ 
					annotatedPathManager = new AnnotatedPathManager();
				}
			}
		}
		return annotatedPathManager;
	}


	private void init() throws IOException {
		String jarName = System.getProperty("java.class.path");
		final File jarFiletest = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.println(jarName);
		if(jarFiletest.isFile()){
			JarFile jarFile = new JarFile(jarName);
			Enumeration<JarEntry> enum1 = jarFile.entries();
		    while (enum1.hasMoreElements()) {
		        JarEntry entry = enum1.nextElement();
		        if (entry.isDirectory()){
		        	//System.out.println(entry.getName());
		        	continue;
		        }else if(entry.getName().contains("com/kisszo/news/action/")){
		        	processJarFile(entry.getName());
		        }
		    }
		}else{
			Enumeration<URL> systemSResources = this.getClass().getClassLoader().getResources("com/kisszo/news/action/");
			if(systemSResources != null){
				while(systemSResources.hasMoreElements()){
					URL fileURL = systemSResources.nextElement();
					System.out.println("Processing file1 : " +fileURL.getPath());
					File file = new File(URLDecoder.decode(fileURL.getPath()));
					processFile(file);
				}
			}
		}
	}
	

	
	private Class resolvejarClass(String path){
		int classStartPoint = path.indexOf("com/kisszo/");
		int classEndPoint = path.indexOf(".class");
		String classPart = path.substring(classStartPoint, classEndPoint);
		classPart = classPart.replaceAll("/", ".");
		System.out.println(classPart);
		Class classObject = null;
		try {
			classObject = Class.forName(classPart);
		}catch (ClassNotFoundException e) {
			log.error("error in loading class", e);
		}
		return classObject;
	}
	
	
	private void processJarFile(String path){
		Class classDefinition = resolvejarClass(path); 
		System.out.println("Processing Class Name : " +classDefinition.getName());
		Annotation[] annotation = classDefinition.getAnnotations();
		System.out.println("Declared Annotation count:"+ annotation.length);
		if(annotation != null && annotation.length > 0 ){
			for(Annotation ann : annotation){
				System.out.println("Declared Annotation : " + ann);
				if(ann instanceof Path){
					collectPathDetails(classDefinition, (Path)ann);
					break;
				}
			}
		}
	}
	
	
	private Class resolveClass(String path){
		path = path.replace("/", "\\");
		if(path.indexOf("com\\kisszo") == -1 || path.indexOf(".class") == -1){
			return null;
		}
		
		int classStartPoint = path.indexOf("com\\kisszo");
		int classEndPoint = path.indexOf(".class");
		String classPart = path.substring(classStartPoint, classEndPoint);
		classPart = classPart.replaceAll("\\\\", ".");
		Class classObject = null;
		try {
			classObject = Class.forName(classPart);
		}catch (ClassNotFoundException e) {
			log.error("error in loading class", e);
		}
		return classObject;
		
	}
	
	private void processFile(File file){
		System.out.println("test1");
		if(file.isFile()){
			if(file.getName().endsWith(".class")){
					System.out.println("Processing File Path : " +file.getAbsolutePath());
					Class classDefinition = resolveClass(file.getAbsolutePath()); 
					System.out.println("Processing Class Name : " +classDefinition.getName());
					Annotation[] annotation = classDefinition.getAnnotations();
					System.out.println("Declared Annotation count:"+ annotation.length);
					if(annotation != null && annotation.length > 0 ){
						for(Annotation ann : annotation){
							System.out.println("Declared Annotation : " + ann);
							if(ann instanceof Path){
								collectPathDetails(classDefinition, (Path)ann);
								break;
							}
						}
					}
			}
		}else if(file.isDirectory()){
			System.out.println("file count"+file.isDirectory());
			File[] childFiles = file.listFiles();
			for(File childFile : childFiles){
				System.out.println("Processing Directory : " + childFile.getAbsolutePath());
				processFile(childFile);
			}
		}
	}
	
	
	private void collectPathDetails(Class classDec, Path classLevelPathDefinition){
		String classLevelPath = classLevelPathDefinition.path();
		Method[] methods = classDec.getMethods();
		if(methods != null){
			for(Method m : methods){
				Annotation[] decMethodAnn = m.getDeclaredAnnotations();
				if(decMethodAnn != null){
					for(Annotation methodAnn : decMethodAnn){
						if(methodAnn instanceof Path){
							String methodLevelPath = ((Path) methodAnn).path();
							String fullPath = classLevelPath + methodLevelPath;
							System.out.println(fullPath);
							PathImplementor pathImplementor = new PathImplementor(classDec, m);
							paths.put(fullPath, pathImplementor);
						}
					}
				}
			}
		}
	}
	
	public PathImplementor getActionImplementor(String path){
		return paths.get(path);
	}
	
	public Set<String> getAllURI(){
		return paths.keySet();
	}
	
	public static class PathImplementor{
		
		Class classDec = null;
		Method method = null;
		
		public PathImplementor(Class classDec, Method method) {
			this.classDec = classDec;
			this.method = method;
		}
		
		public Class getClassDec() {
			return classDec;
		}
		public void setClassDec(Class classDec) {
			this.classDec = classDec;
		}
		public Method getMethod() {
			return method;
		}
		public void setMethod(Method method) {
			this.method = method;
		}
		
		
		
	}
	
}
