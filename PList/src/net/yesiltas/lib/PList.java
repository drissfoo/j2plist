package net.yesiltas.lib;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
public class PList {
	private static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"+
				"<plist version=\"1.0\">\n";
	private static final String footer = "</plist>";
	private static Exception invalidObjectType=new Exception("Invalid object type.");
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public PList() {
		super();
	}
	public PList(SimpleDateFormat dateFormat) {
		super();
		this.dateFormat = dateFormat;
	}
	public String encode(Object object) throws Exception{
		return header+convertToPlist(object)+footer;
	}	
	private String convertToPlist(Object object) throws Exception{
		StringBuffer resultBuffer = new StringBuffer();
		if (object instanceof ArrayList<?>){
			resultBuffer.append(convertArrayToPlist(object));	
		} else if (object instanceof Map<?,?>) {
			resultBuffer.append("\t<dict>\n");
			resultBuffer.append(convertMapToPlist(object));
			resultBuffer.append("\t</dict>\n");
		} else if (isBean(object)) {
			resultBuffer.append("\t<dict>\n");
			resultBuffer.append("\t<key>"+object.getClass().getSimpleName().toLowerCase(Locale.ENGLISH)+"</key>\n");
			resultBuffer.append(convertBeanToPlist(object));
			resultBuffer.append("\t</dict>\n");
		} else {
			resultBuffer.append(convertElementToPlist(object));
		}
		return resultBuffer.toString();		
	}	
	private String convertArrayToPlist(Object array) throws Exception{
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("\t<array>\n");
		for (Object elem:((ArrayList<?>)array) ){
			resultBuffer.append(convertToPlist(elem));
		}
		resultBuffer.append("\t</array>\n");
		return resultBuffer.toString();
	}	
	private String convertMapToPlist(Object map) throws Exception{
		StringBuffer resultBuffer = new StringBuffer();		
		Set<?> keySet = (Set<?>)((Map<?,?>)map).keySet();
		Iterator<?> it = keySet.iterator();
		while (it.hasNext()){
			Object key = it.next();
			Object value = ((Map<?,?>)map).get(key);
			if (value instanceof ArrayList<?>) {
				resultBuffer.append("\t<key>"+key+"</key>\n");
				resultBuffer.append(convertArrayToPlist(value));
			} else if (value instanceof Map<?,?>){
				resultBuffer.append(convertMapToPlist(value));
			} else if (isBean(value)) {
				resultBuffer.append("\t<key>"+key+"</key>\n");
				resultBuffer.append(convertBeanToPlist(value));
			} else {
				resultBuffer.append("\t<key>"+key+"</key>\n");
				resultBuffer.append(convertElementToPlist(value));
			}
		}
		return resultBuffer.toString();
	}
	
	private String convertElementToPlist(Object elem) throws Exception{
		if (elem instanceof ArrayList<?>){
			return convertArrayToPlist(elem);
		}
		if (elem instanceof String){
			return "\t<string>"+(String)elem+"</string>\n";
		} else if (elem instanceof Date){
			return "\t<date>"+dateFormat.format((Date)elem)+"</date>\n";
		} else if (elem instanceof Integer){
			return "\t<integer>"+(Integer)elem+"</integer>\n";
		} else if (elem instanceof Float){
			return "\t<real>"+(Float)elem+"</real>\n";
		} else if (elem instanceof Double){
			return "\t<real>"+(Double)elem+"</real>\n";
		} else if (elem instanceof Boolean){
			if((Boolean)elem){
				return "\t<true/>\n";
			}else{
				return "\t<false/>\n";
			}
		}else {
			throw invalidObjectType;	
		}
	}	
	
	private String convertBeanToPlist(Object object) throws Exception{
		StringBuffer resultBuffer = new StringBuffer();		
		resultBuffer.append("\t<dict>\n");
		Class<?> cls = object.getClass();
		Method methods[] = cls.getMethods();
		for (Method method:methods){
			String methodName = method.getName();
			if (!methodName.equals("getClass") && methodName.startsWith("get")){
				resultBuffer.append("\t<key>"+methodName.substring(3).toLowerCase(Locale.ENGLISH)+"</key>\n");
				Object retobj = method.invoke(object, new Object[0]);	
				resultBuffer.append(convertElementToPlist(retobj));
			}			
		}
		resultBuffer.append("\t</dict>\n");
		return resultBuffer.toString();
	}
	
	private boolean isBean(Object object){
		Class<?> cls = object.getClass();
		Field[] fieldlist = cls.getDeclaredFields();
		for (Field field:fieldlist){
			if (hasGetter(object,field)){
				return true;
			}
		}
		return false;
	}
	
	private boolean hasGetter(Object object,Field field){
		Class<?> cls = object.getClass();
		Method methods[] = cls.getMethods();
		String fieldName = field.getName();
		for (Method method:methods){
			String methodName = method.getName();
			if (methodName.toLowerCase(Locale.ENGLISH).equals("get"+fieldName.toLowerCase(Locale.ENGLISH))){
				return true;
			}			
		}
		return false;
	}
}
