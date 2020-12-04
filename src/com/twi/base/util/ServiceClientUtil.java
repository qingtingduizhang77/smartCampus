package com.twi.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class ServiceClientUtil {

	/**

 一、取收费信息信息
1.参数类型：JSon字符串
2.格式：{"type":"query","subType":"power_rate(取电表信处 )","orgId":"学校Id","param":查询条件json}
3.成功返回值： {"return_code":"SUCCESS","return_obj":"jsonstr"}
4.失败返回值： {"return_code":"FAIL","return_msg":"***"}


二、生成收费信息
1.参数类型：JSon字符串
2.格式：{"type":"add_order_info","subType":"add/update/del","orgId":"学校Id","payCode":"收费类型编码","orderNo":"商户订单号","money":"缴费金额","param":附加信息json}
3.成功返回值： {"return_code":"SUCCESS","return_obj":"jsonstr"}
4.失败返回值： {"return_code":"FAIL","return_msg":"***"}



一(1)、取电表信息
1.参数类型：JSon字符串
2.格式：{"type":"query","subType":"power_rate","orgId":"学校Id","param":{"metercode":"电表编号"}}
3.成功返回值：{"Code":"0","Msg":"成功","RCount":1,"ObjSet":[{"MeterCode":"620750","MeterName":"7栋101室",
"CustomerCode":"92239819","CustomerName":"微信充值用户","UseElectricity":"2624.0400",
"RemainingAmount":"-2183.9800","Status":"1"}]}
4.失败返回值： {"return_code":"FAIL","return_msg":"***"}

二(1)、生成充值电费信息
1.参数类型：JSon字符串
2.格式：{"type":"businessData","subType":"add","orgId":"学校Id","payCode":"收费类型编码","orderNo":"商户订单号",
"money":"缴费金额","param":{"meterCode":"电表编号","meterName":"电表名称/位置","remark":"备注"}}
3.成功返回值：{\"Code\":\"0\",\"Msg\":\"成功\"}
4.失败返回值： {"return_code":"FAIL","return_msg":"***"}

	 **/

	/**
	 * 统一调用服务接口
	 * @param param
	 * @return
	 */
	public static String csExtend(String param) {
		String jsonResult = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-ReturnIsNULL"));
		Socket socket = null;
		OutputStream os = null;
		PrintWriter pw = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			String mqsUrl = PropertiesUtils.getProperty("serviceUrl");
			String mqsPort = PropertiesUtils.getProperty("servicePort");
			//1.创建客户端Socket，指定服务器地址和端口
			socket = new Socket(mqsUrl, new Integer(mqsPort.trim()));
			//2.获取输出流，向服务器端发送信息
			os = socket.getOutputStream();//字节输出流
			pw = new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);//将输出流包装为打印流
			pw.write(param);
			pw.flush();
			socket.shutdownOutput();//关闭输出流
			//3.获取输入流，并读取服务器端的响应信息
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String info = null;
			while((info=br.readLine())!=null) {
				jsonResult = ResultFormat.jsonStrSuccess(info);
			}
		} catch (ConnectException e) {
			e.printStackTrace();
			jsonResult = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-ConnectException.ServiceClientUtil"));
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-Exception.ServiceClientUtil"));
		} finally {
			//关闭资源
			try {
				if(br!=null)
					br.close();
				if(is!=null)
					is.close();
				if(pw!=null)
					pw.close();
				if(os!=null)
					os.close();
				if(socket!=null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				jsonResult = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-IOException.ServiceClientUtil"));
			}
		}
		return jsonResult;
	}

}
