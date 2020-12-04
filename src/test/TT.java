package test;

import com.twi.base.util.MD5;

public class TT {

	public static void main(String[] args) {
		
		
		try {
			//5f4dcc3b5aa765d61d8327deb882cf99
			System.out.println(MD5.getMD5Encode("password"));
			System.out.println(MD5.getMD5Encode("password").toUpperCase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
