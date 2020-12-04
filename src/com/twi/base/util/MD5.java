package com.twi.base.util;


import java.security.MessageDigest;
import java.security.*;

/**
 * MD5加密
 * @author Administrator
 *
 */
public class MD5 {

    private final static String[] hexDigits = {
        "0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        //StringBuffer ��ͬ�����̰߳�ȫ�ģ�Ч�ʵͣ�StringBuilder�ǲ�ͬ���ģ�Ч�ʸ�
    	StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * @param origin String
     * @return String
     * @throws Exception
     */
    public static String getMD5Encode(String origin) throws Exception {
        if (!inited) {
            throw new Exception("MD5 �㷨ʵ���ʼ������");
        }
        if (origin == null) {
            return null;
        }
        byte[] temp = null;

        /*
         ��Ϊ����ʵ���õ��Ǿ�̬�ģ�����Ϊ�˱������̴߳�������ͬ��������
         �������ͬ�������Ĵ�۱ȴ���ʵ��Ĵ��С������ͬʱ���������Ŀ����Ժ�С���������ͬʱ���ö�ȴ�����⡣
         */
        synchronized (md) {
            temp = md.digest(origin.getBytes());
        }

        return byteArrayToHexString(temp);

    }

    private static MessageDigest md = null;
    private static boolean inited = false;
    static {
        try {
            md = MessageDigest.getInstance("MD5");
            inited = true;
        }
        catch (NoSuchAlgorithmException ex) {
            inited = false;
        }
    }

    public static void main(String[] args) {
        try {
//            System.err.println(getMD5Encode("01,18,10010574273,20080529,��С��,511502198907255181,406.47,,,02,9558822011002506575,zΰǿ,���ú�,������,"));
            System.out.println(getMD5Encode("password").toUpperCase());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
