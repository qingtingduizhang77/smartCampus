package com.twi.base;

import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;



import com.twi.security.domain.SSCUserDetails;

public class WebHelper {
	public static UserDetails getLogonUser() {
		Object obj = SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (obj instanceof UserDetails) {
			return (UserDetails) obj;
		}
		return null;
	}

	public static SSCUserDetails getUser() {
		UserDetails obj = getLogonUser();
		if (obj instanceof SSCUserDetails) {
			return (SSCUserDetails) obj;
		}
		return null;
	}

	public static String getUserId() {
		return getUser().getUserId();
	}


	

	
	/**
	 * 获取IP地址对象
	 * @return
	 */
    public static InetAddress getInetAddress(){
        try{
            return InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            System.out.println("unknown host!");
        }
        return null;
    }  
  
   
  
	
	

}
