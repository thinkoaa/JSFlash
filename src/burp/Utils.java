package burp;


import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class Utils {
    public static Invocable  invocable;
    public static String functionName;
    
//    
//    public static void main(String[] args) throws Exception {
//    	functionName="test";
//    	initJsEngine("C:\\Users\\LXP\\Desktop\\encr.js");
//    	System.out.println(Utils.invocable.invokeFunction("hello", "hello world!").toString());
//	}
    
     public  static  void initJsEngine(String javaScriptPath) throws Exception{
    	// 获取JS引擎
 		ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");
 		FileReader fr = new FileReader(javaScriptPath);
 		se.eval(fr);
 		fr.close();
 		invocable= (Invocable) se;
 	 
     }
    

 

    public static String getBanner(){
    
        String hello =
                  "==	" + BurpExtender.extensionName +"-"+BurpExtender.version+ "\n"
                + "==	hello\n"
                + "==	anthor: thinkoaa\n"
                + "==	email: 990448338@qq.com\n"
        		+ "==	github: https://github.com/thinkoaa/JSFlash\n";
        
        
        return hello;
    }

	//从request中根据开始、结束位置截取字符串，调用js对应方法处理之后，返回处理后的新的request
	public static String changeRequest(String requestStr,String startStr,String endStr,String functionName) throws NoSuchMethodException, ScriptException {
 		 //windows下这样处理
		startStr=startStr.replace("\n", "\r\n");
		endStr=endStr.replace("\n", "\r\n");
		if(requestStr.indexOf(startStr)==-1) {//如果没有要包含的字符
			return requestStr;
		}
		int startStrIndex=requestStr.indexOf(startStr) + startStr.length();
		
		int entStrIndex=requestStr.indexOf(endStr);
		if(endStr==null||endStr.trim().equals("")) {
			entStrIndex=requestStr.length();
		} 
		String getTheStr=requestStr.substring(startStrIndex,entStrIndex).trim();
	 String newStr=Utils.invocable.invokeFunction(functionName.trim(), getTheStr).toString();
		
		String subStartStr=requestStr.substring(0, startStrIndex);
		String subEndStr=requestStr.substring(entStrIndex,requestStr.length());
		
		return subStartStr+newStr+subEndStr;
		
	}
 
}
