package com.twi.base.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.twi.base.exceptions.BusinessException;

/**
 * Created by zhengjc
 */
public class HttpClientUtils {

	protected static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	
    private final static int connectionTimeout = 6000;
    private final static int soTimeout = 10000;

    public final static String CHARSET = "UTF-8";

    public final static String APIURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     *
     * @param datas 数据
     */
    public static String post(Map<String, String> datas, String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(soTimeout)
                .setConnectTimeout(connectionTimeout).build();
        httpPost.setConfig(requestConfig);
//        String reqBody = getXml(datas);

        try {
			String reqBody = WXPayUtil.mapToXml(datas);
            StringEntity entityParams = new StringEntity(reqBody, CHARSET);
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setEntity(entityParams);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                StatusLine status = response.getStatusLine();
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    return result;
                }else{
                    throw new BusinessException(status.getStatusCode() + "");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 数据签名
     * @param charset
     * @param signKey
     * @param params
     * @param isLowerCase
     * @return
     */
    public static String sign(String charset, String signKey, Map<String, String> params, boolean isLowerCase) {
        params = paraFilter(params); // 过滤掉一些不需要签名的字段
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        
        buildPayParams(buf, params, isLowerCase);

        buf.append("&key=").append(signKey.trim());
        String preStr = buf.toString();
        String sign = md5(preStr, charset);
        return sign.toUpperCase();
    }

    /**
     * 过滤参数
     * @param sArray
     * @return
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>(sArray.size());
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 排序
     * @param sb
     * @param payParams
     * @param isLowerCase
     */
    public static void buildPayParams(StringBuilder sb,Map<String, String> payParams,boolean isLowerCase){
        List<String> keys  = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys, Collator.getInstance(java.util.Locale.ENGLISH));
        for(String key : keys){
            if (isLowerCase){
                sb.append(key.toLowerCase()).append("=");
            } else {
                sb.append(key).append("=");
            }
            sb.append(payParams.get(key)).append("&");

        }
        sb.setLength(sb.length() - 1);
        logger.info("======------======:" + sb.toString());
    }

    /**
     * MD5加密
     * @param text
     * @param charset
     * @return
     */
    public static String md5(String text,String charset) {
        return DigestUtils.md5Hex(getContentBytes(text, charset));
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(String.format("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:%s",charset));
        }
    }

    public static String toXml(Map<String, String> params, String tag){
        StringBuilder buf = new StringBuilder();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        buf.append("<"+tag+">");
        for(String key : keys){
            buf.append("<").append(key).append(">");
            buf.append("<![CDATA[").append(params.get(key)).append("]]>");
            buf.append("</").append(key).append(">\n");
        }
        buf.append("</"+tag+">");
        return buf.toString();
    }

    public static Object getObjectFromXML(String xml, Class<?> tClass) {
        Object obj=null;
        try {
            XStream xStreamForResponseData = new XStream();
            xStreamForResponseData.alias("xml", tClass);
            xStreamForResponseData.ignoreUnknownElements();//暂时忽略掉一些新增的字段
            obj=xStreamForResponseData.fromXML(xml);
        } catch (Exception e) {
            logger.error("getXmlFromObject Exception:",e);
        }
        return obj;
    }
    
    /**
     * 检验微信返回的数据并校验签名是否一致
     *
     * @param xmlResponse
     * @return Boolean
     */
    public static Boolean validateResponseXmlToMap(String xmlResponse) {
        Boolean  flag=false;
        if (StringUtils.isEmpty(xmlResponse)) {
            throw new RuntimeException("传入请求微信支付返回的数据为空");
        }
        // 处理微信返回的数据进行验证
//        Map<String, String> xmlToMap = XmlUtil.xmlToMap(xmlResponse);
//        logger.info("请求微信支付,返回的数据转换成Map" + xmlToMap);
//        // 1.判断返回值是否成功 return_code 此值返回成功并不代表成功
//        String return_code = xmlToMap.get("return_code");
//        String return_msg = xmlToMap.get("return_msg");
//        logger.info("微信支付,返回的通信标识" + return_code);
//        logger.info("微信支付,返回的信息" + return_msg);
//        if (StringUtils.isNotEmpty(return_code) && return_code.equals("SUCCESS")) {
//            // 需要判断此值
//            String result_code = xmlToMap.get("result_code");
//            logger.info("微信支付,返回的成功标识" + result_code);
//            if (StringUtils.isNotEmpty(result_code) && result_code.equals("SUCCESS")) {
//                // 校验返回的sign是否一致
//                validateSign(xmlToMap, Constants.WX_KEY);
//                flag=true;
//                return flag;
//            }
//        }
        return flag;
    }


    
    
    /**
     * 校验微信支付的sign
     *
     * @param parameterMap
     * @param key          void
     */
    public static boolean validateSign(Map<String, String> parameterMap, String key) {
        String sign = parameterMap.get("sign");
        logger.info("微信支付,传入的sign进行校验" + sign);
        if (StringUtils.isEmpty(sign)) {
            logger.info("微信支付,sign参数为空!");
            return false;
        }
        String md5Hex = getSign(parameterMap, key);
        if (!md5Hex.equals(sign.toUpperCase())) {
            logger.info("微信支付,签名错误");
            return false;
        }
        return true;
    }

    
    /**
     * 获取sign值
     *
     * @param parameterMap
     * @param key
     * @return String
     */
    public static String getSign(Map<String, String> parameterMap, String key) {
        // 将Map转换为TreeMap
        Set<Map.Entry<String, String>> parameterMapSet = parameterMap.entrySet();
        Iterator<Map.Entry<String, String>> hashMapIterator = parameterMapSet.iterator();
 
        Map<String, String> treeMap = new TreeMap<String, String>();
        while (hashMapIterator.hasNext()) {
            Map.Entry<String, String> param = hashMapIterator.next();
            if (!"sign".equals(param.getKey())) {
                treeMap.put(param.getKey(), param.getValue());
            }
        }
        // 拼接字符串
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, String>> treeMapSet = treeMap.entrySet();
        Iterator<Map.Entry<String, String>> treeMapIterator = treeMapSet.iterator();
        while (treeMapIterator.hasNext()) {
            Map.Entry<String, String> param = treeMapIterator.next();
            // 校验空值
            if (StringUtils.isEmpty(param.getValue())) {
                if (treeMapIterator.hasNext()) {
                } else {
                    sb.replace(sb.toString().length() - 1, sb.toString().length(), "");
                }
                continue;
            }
            sb.append(param.getKey());
            sb.append("=");
            sb.append(param.getValue());
            if (treeMapIterator.hasNext()) {
                sb.append("&");
            }
        }
        if (StringUtils.isEmpty(sb.toString())) {
            throw new RuntimeException("传入的参数为空");
        }
        // 拼接key
        sb.append("&key=").append(key);
        logger.info("微信支付,检验的拼接的字符串=" + sb.toString());
        String md5Hex = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        return md5Hex;
    }

    public static String getXml(Map<String, String> params){
    	StringBuffer paramBuffer = new StringBuffer();
		paramBuffer.append("<xml>");
		paramBuffer.append("<appid>"+ params.get("appid")+"</appid>");
		paramBuffer.append("<body>"+params.get("body")+"</body>");
		paramBuffer.append("<mch_id>"+params.get("mch_id")+"</mch_id>");
		paramBuffer.append("<nonce_str>"+params.get("nonce_str")+"</nonce_str>");
		paramBuffer.append("<notify_url>"+params.get("notify_url")+"</notify_url>");
		paramBuffer.append("<openid>"+params.get("openid")+"</openid>");
		paramBuffer.append("<out_trade_no>"+params.get("out_trade_no")+"</out_trade_no>");
		paramBuffer.append("<total_fee>"+params.get("total_fee")+"</total_fee>");
		paramBuffer.append("<trade_type>"+params.get("trade_type")+"</trade_type>");
		paramBuffer.append("<sign_type>"+params.get("sign_type")+"</sign_type>");
		paramBuffer.append("<sign>"+params.get("sign")+"</sign>");
		paramBuffer.append("</xml>");
    	return paramBuffer.toString();
    }
    
    public static Map<String, String> xmlToMap(String xml, String charset) throws UnsupportedEncodingException, DocumentException{

    	Map<String, String> respMap = new HashMap<String, String>();

    	SAXReader reader = new SAXReader();
    	Document doc = reader.read(new ByteArrayInputStream(xml.getBytes(charset)));
    	Element root = doc.getRootElement();
    	xmlToMap(root, respMap);
    	return respMap;
    }

   public static Map<String, String> xmlToMap(Element tmpElement, Map<String, String> respMap){
    	if (tmpElement.isTextOnly()) {
    	respMap.put(tmpElement.getName(), tmpElement.getText());
    	return respMap;
    	}

    	@SuppressWarnings("unchecked")
    	Iterator<Element> eItor = tmpElement.elementIterator();
    	while (eItor.hasNext()) {
    	Element element = eItor.next();
    	xmlToMap(element, respMap);
    	}
    	return respMap;
    }
    
}
