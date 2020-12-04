package com.twi.base.exceptions;


import org.springframework.http.HttpStatus;

/**
 * 业务异常
 * @author zhengjc
 *
 */
public class BusinessException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4185629578148248412L;
	/**
	 * 异常编码
	 */
	private String code;
	
	private HttpStatus status;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public BusinessException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	public BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
	public BusinessException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}
	
	

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}


	public BusinessException(Throwable cause) {
		super(cause);
	}
	public BusinessException(HttpStatus status, Throwable cause) {
		super(cause);
		this.status = status;
	}
}
