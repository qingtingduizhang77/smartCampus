package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Pt {

	 public static String ryPushMsg(String param)
	    {
	    	String jsonResult = "";
	           try {
	    		
	    		String mqsUrl="192.168.1.211";
	    		String mqsPort="9888";
	            //1.创建客户端Socket，指定服务器地址和端口
	            Socket socket=new Socket(mqsUrl, new Integer(mqsPort.trim()));
	            //2.获取输出流，向服务器端发送信息
	            OutputStream os=socket.getOutputStream();//字节输出流
	            PrintWriter pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);//将输出流包装为打印流
	            pw.write(param);
	            pw.flush();
	            socket.shutdownOutput();//关闭输出流
	            //3.获取输入流，并读取服务器端的响应信息
	            InputStream is=socket.getInputStream();
	            BufferedReader br=new BufferedReader(new InputStreamReader(is));
	            String info=null;
	            boolean faly=true;
	            while((info=br.readLine())!=null){
	            	
	            	jsonResult= info;
	            }
	            //4.关闭资源
	            br.close();
	            is.close();
	            pw.close();
	            os.close();
	            socket.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	           jsonResult="{\"code\":\"1\",\"msg\":\"失败\"}";
	        }
	    	return jsonResult;
	    }
	 
	 public static void main(String[] args) {
			
		//组装参数Start
		 Map<String,Object> jsonMap=new HashMap<String,Object>() ;
		 Map<String,Object> param=new HashMap<String,Object>() ;
		 jsonMap.put("type", "power_rate");
		 param.put("orgId", "0af3ce9c-184e-4984-841d-3a4ea8ee33e0");
		 param.put("metercode", "620750");
		 jsonMap.put("param", param);
		 JSONObject jsonObject =JSONObject.fromObject(jsonMap);
		 
		//组装参数end
		 try{
	     //调用接口
		 String reJson= ryPushMsg(jsonObject.toString());
		 //处理返回结果
		 if(reJson!=null)
		 {
			 System.out.println(reJson);
			 JSONObject  jsonObject1=JSONObject.fromObject(reJson);
			 
			
			 int code=jsonObject1.getInt("Code");
         	 int RCount=jsonObject1.getInt("RCount");
         	 if(code==0 && RCount>0)
         	 {
         		 
         		 JSONArray  objSet=jsonObject1.getJSONArray("ObjSet");
         		 
         		 if(objSet!=null && objSet.size()>0)
         		 {
         			  			
         		 }
         		 
         		 
         	 }
         	
         	 
		 }
		
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }

		}

	 
}
