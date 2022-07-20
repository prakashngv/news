package com.kisszo.news.netty.manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.kisszo.news.exceptions.KisszoException;

import groovy.json.JsonException;

public class KisszoParser {
	private static KisszoParser instance = null;
	//private JsonParser parser = new JsonParser();
	public KisszoParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static KisszoParser getInstance(){
		if(instance == null){
			synchronized (KisszoParser.class) {
				if(instance == null){
					instance = new KisszoParser();
				}
			}
		}
		return instance;
	}
	
	public JsonArray parseJson(String data){
		JsonParser parser = new JsonParser();
		if(data != null){
			try{
				JsonElement elm = parser.parse(data);
				return elm.getAsJsonObject().getAsJsonArray("result");
			}catch (Throwable e) {
				// TODO: handle exception
				new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			}
		}
		return null;
	}
	
	public JsonObject parseJsonGetObject(String data){
		JsonParser parser = new JsonParser();
		if(data != null){
			try{
				JsonElement elm = parser.parse(data);
				return elm.getAsJsonObject().getAsJsonArray("result").size()>0?elm.getAsJsonObject().getAsJsonArray("result").get(0).getAsJsonObject():null;
			}catch (Throwable e) {
				// TODO: handle exception
				new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			}
		}
		return null;
	}
	
	public Map<String, Object> stringToMapMQ(String value){
		if(value != null && !value.equalsIgnoreCase("")){
			try{
				value = value.substring(1, value.length()-1); 
				String[] keyValuePairs = value.replaceAll("([{}'\"])","").split(",");
				Map<String, Object> map = new HashMap<String, Object>();               

				for(String pair : keyValuePairs)
				{
					String Key = "";
					String Value = "";
				    String[] entry = pair.split("=");   
				    if(entry.length > 1){
				    	 Key = entry[0].trim();
						 Value = entry[1].trim();
				    }else{
				    	Key = entry[0].trim();
				    }
				    map.put(Key, Value);
				}
				return map;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
	public JsonObject parseJsontoObject(String data){
		JsonParser parser = new JsonParser();
		if(data != null){
			try{
				JsonElement elm = parser.parse(data);
				return elm.getAsJsonObject();
			}catch (Throwable e) {
				// TODO: handle exception
				e.printStackTrace();
				new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			}
		}
		return null;
	}
	
	public JsonElement parseJsontoEelemnt(String data){
		JsonParser parser = new JsonParser();
		if(data != null){
			try{
				JsonElement elm = parser.parse(data);
				return elm;
			}catch (Throwable e) {
				// TODO: handle exception
				new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			}
		}
		return null;
	}
	
	public JsonArray parseJsonArray(String data){
		JsonParser parser = new JsonParser();
		if(data != null){
			try{
				JsonElement elm = parser.parse(data);
				return elm.getAsJsonArray();
			}catch (JsonSyntaxException e) {
				// TODO: handle exception
				new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			}
		}
		return null;
	}
	
	public int getSMSCount(String msg){
		int msgLength = msg.length();
		if(msgLength > 0){
			if(msgLength <= 160){
				return 1;
			}else if(msgLength > 160 && msgLength <= 306){
				return 2;
			}else if(msgLength > 306 && msgLength <= 459){
				return 3;
			}else if(msgLength > 459 && msgLength <= 612){
				return 4;
			}else if(msgLength > 612 && msgLength <= 765){
				return 5;
			}else{
				return 6;
			}
		}else{
			return 0;
		}
	}
	
	public boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (Exception ex) {
	        try {
	            new JSONArray(test);
	        } catch (Exception ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public boolean isJSONArray(String test) {
	    try {
	        new JSONArray(test);
	    } catch (JSONException ex) {
	        return false;
	    }
	    return true;
	}
	
	public Map<String,Object> convertJsontoMapper(String json){
		Map<String,Object> resultMap = new HashMap<String,Object>();
        ObjectMapper mapperObj = new ObjectMapper();
         
        try {
            resultMap = mapperObj.readValue(json, new TypeReference<HashMap<String,Object>>(){});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
        }
		return resultMap;
	}
	
	public Map<String, Object> stringToMap(String value){
		value = value.substring(1, value.length()-1); 
		String[] keyValuePairs = value.replaceAll("([{}'\"])","").split(",");
		Map<String, Object> map = new HashMap<String, Object>();               

		for(String pair : keyValuePairs)
		{
		    String[] entry = pair.split(":");   
		    String Key = entry[0].trim();
		    String Value = entry[1].trim();
		    map.put(Key, Value);
		}
		return map;
	} 
	
	//inputArray - new aggregation Input array ; resultArray - resultArray
	public JsonArray jsonAggregation(JsonArray inputArray,JsonArray resultArray,String matchingKey){
		
		for (JsonElement jsonElement : inputArray) {
			JsonObject inuputObject = jsonElement.getAsJsonObject();
			boolean flag = false;
			for (JsonElement jsonElement1 : resultArray) {
				JsonObject resultObject = jsonElement1.getAsJsonObject();
				if(resultObject.get(matchingKey).getAsString().equalsIgnoreCase(inuputObject.get(matchingKey).getAsString())){
					flag = true;
					Set<Entry<String, JsonElement>> entrySet = resultObject.entrySet();
					for(Map.Entry<String,JsonElement> entry : entrySet){
						if(inuputObject.getAsJsonPrimitive(entry.getKey()).isNumber()){
							try{
								resultObject.addProperty(entry.getKey(), (inuputObject.get(entry.getKey()).getAsDouble() + resultObject.get(entry.getKey()).getAsDouble()));
							}catch(Exception e){e.printStackTrace();
							
								try{
									resultObject.addProperty(entry.getKey(), (inuputObject.get(entry.getKey()).getAsInt() + resultObject.get(entry.getKey()).getAsInt()));
								}catch(Exception e1){e1.printStackTrace();}
							
							}
						}	
					}
				}
			}
			if(!flag){
				resultArray.add(inuputObject);
			}
		
		}
		return resultArray;
		
	}
	
	public List<Map<String, Object>> JsonArrayToList(JsonArray jsonArray){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try{
	        if(jsonArray != null && jsonArray.size() > 0){
	        	for(JsonElement element : jsonArray){
	        		
	        		 Map<String, Object> map = new HashMap<String, Object>();
	        		 JsonObject obj = element.getAsJsonObject(); //since you know it's a JsonObject
	        		 Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
	        		
	        		 for (Map.Entry<String, JsonElement> entry: entries) {
	        		     map.put(entry.getKey(), entry.getValue().getAsString());
	        		 }
	        		 list.add(map);
	        	}
	        }
        }catch (Exception e) {
			// TODO: handle exception
        	new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
		}
        return list;
    }
    
    	
	public JsonArray sortingByField(List<JsonObject> jsonList, String sortingKeyField){
		if(jsonList != null && jsonList.size() > 0 && sortingKeyField != null && !sortingKeyField.equalsIgnoreCase("")){
			Collections.sort(jsonList, new Comparator<JsonObject>() {
			    @Override
			    public int compare(JsonObject jsonObjectA, JsonObject jsonObjectB) {
			    	int  compare = 0;
			        try
			        {
			        	String  keyA = jsonObjectA.get(sortingKeyField).getAsString();
			        	String  keyB = jsonObjectB.get(sortingKeyField).getAsString();
			            compare = keyA.compareTo(keyB);
			        }
			        catch(JsonException e)
			        {
			           e.printStackTrace();
			           new KisszoException("Jsoon parse error accured : "+e.getMessage(),e);
			        }
			        return compare;
			    }
			});
			
			JsonArray sortServiceArray = new JsonArray();
			for (int i = 0; i < jsonList.size(); i++) {
				sortServiceArray.add(jsonList.get(i));
			}
			return sortServiceArray;
		}
		return null;
	}	
	
	public String pojoToJsonString(Object pojo){
		String jsonStr = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try { 
            jsonStr =  ow.writeValueAsString(pojo);
//            System.out.println("resp:"+jsonStr); 
            return jsonStr;
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
            return jsonStr;
        } 
		
	}
	
	public JsonObject pojoToJsonObject(Object pojo){
		String jsonStr = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		JsonObject obj = new JsonObject();
        try { 
        	jsonStr =  ow.writeValueAsString(pojo);
//            System.out.println("resp:"+jsonStr); 
            obj = this.parseJsontoObject(jsonStr);
            return obj;
        } 
  
        catch (IOException e) { 
            e.printStackTrace(); 
            //return obj;
        }
		return obj; 
		
	}
	
	public Object jsonObjectToPojo(JsonObject jsonObject,Object object){
        Gson gson = new Gson();
		Object obj = gson.fromJson(jsonObject, object.getClass());
		return obj; 
	}
	
	public JsonObject mapToJsonObject(Map<String, Object> data){
		if(data != null){
			try{
				JsonObject onbj = new JsonObject();
				Set set= data.entrySet();
				Iterator itr=set.iterator(); 
				while(itr.hasNext()){ 
					Map.Entry entry=(Map.Entry)itr.next();
					onbj.addProperty(entry.getKey().toString(),entry.getValue().toString()); 
				}
				return onbj;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return null;
	} 
	
	
	public String serialize(Serializable o) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(o);
	    oos.close();
	    return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
	
	public Object deserialize(String s) throws IOException,
		    ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(
		        new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}
	
	public Map<String,JsonObject> convertJsonArrayToMap(JsonArray resultArray, String key) {
		try {
			Map<String,JsonObject> resultMap = new HashMap<>();
			if(resultArray!=null && resultArray.size()>0) {
				for (JsonElement jsonElement : resultArray) {
					JsonObject object = jsonElement.getAsJsonObject();
					object.remove("@fieldTypes");
					object.remove("@type");
					object.remove("@version");
					resultMap.put(object.get(key).getAsString(), object);
				}
			}
			return resultMap;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JsonArray listMapToJsonArray(List<Map<String, Object>> data){
		JsonArray result = new JsonArray();
		if(data != null){
			try{
				for (Map<String, Object> datas : data){
					JsonObject onbj = new JsonObject();
					Set set= datas.entrySet();
					Iterator itr=set.iterator(); 
					while(itr.hasNext()){ 
						Map.Entry entry=(Map.Entry)itr.next();
						onbj.addProperty(entry.getKey().toString(),entry.getValue().toString()); 
					}
					result.add(onbj);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	} 
	
	
}
