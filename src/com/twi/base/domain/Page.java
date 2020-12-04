package com.twi.base.domain;

import java.util.Collections;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 
 * <pre>
 * 注意：
 *     1. 记录序列号是从0开始.
 *     2. 页码是从1开始.
 * </pre>
 * 
 * @param <T>
 *            Page中记录的类型
 */
public class Page<T> {
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	private int pageNo = 1;
	private int pageSize = 0;
	private String orderBy = null;
	private String orderType = null;
	private boolean autoCount = false;
	private List<T> result = Collections.emptyList();
	private long totalCount = 0;
	
	

	public Page() {
	}
	
	public Page(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 获得当前页的页码, 序号从1开始, 默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}
	
	public long getCountPage()
	{
		long countPage=0;
		if(pageNo>0)
		{
			 countPage=totalCount/pageSize;
			 if(totalCount%pageSize>0 )
			 {
				 countPage++;
			 }
		}
		
		return countPage;
	}

	/**
	 * 设置当前页的页码, 序号从1开始,低于1时自动调整为1.
	 * 
	 * @param pageNo
	 *            页码
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = (pageNo < 1) ? 1 : pageNo;
	}

	/**
	 * 设置当前页的页码, 序号从1开始,低于1时自动调整为1.
	 * 
	 * @param pageNo
	 *            页码
	 * @return this
	 */
	public Page<T> pageNo(final int pageNo) {
		setPageNo(pageNo);
		return this;
	}

	/**
	 * 获得每页的记录数量, 默认为0, 表示不分页.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量, 为0表示不分页.
	 * 
	 * @param pageSize
	 *            每页的记录数量
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = (pageSize < 0) ? 0 : pageSize;
	}

	/**
	 * 设置每页的记录数量, 为0表示不分页
	 * 
	 * @param pageSize
	 *            每页的记录数量
	 * @return this
	 */
	public Page<T> pageSize(final int pageSize) {
		setPageSize(pageSize);
		return this;
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置, 序号从0开始.
	 */
	public int getFirst() {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * 获得排序字段, 无默认值. 多个字段用","分隔
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段.
	 * 
	 * @param orderBy
	 *            排序字段. 多个字段用","分隔
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 设置排序字段.
	 * 
	 * @param orderBy
	 *            排序字段. 多个字段用","分隔
	 * @return this
	 */
	public Page<T> orderBy(final String orderBy) {
		setOrderBy(orderBy);
		return this;
	}

	/**
	 * 获取排序方向
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 设置排序方向
	 * 
	 * @param orderType
	 *            排序方向。可选值{@link #ASC}、{@link #DESC}。多个属性用","分隔
	 */
	public void setOrderType(String orderType) {
		orderType = orderType.toLowerCase().trim();
		String[] types = orderType.split(",");
		for (String type : types) {
			if (!ASC.equals(type) && !DESC.equals(type)) {
				throw new IllegalArgumentException("排序方向" + type + "不是合法值");
			}
		}
		this.orderType = orderType;
	}

	/**
	 * 设置排序方向
	 * 
	 * @param orderType
	 *            排序方向。可选值{@link #ASC}、{@link #DESC}。多个属性用","分隔
	 */
	public Page<T> orderType(String orderType) {
		setOrderType(orderType);
		return this;
	}

	/**
	 * 将排序属性转换为数组
	 */
	public String[] toArrayFromOrderBy() {
		return (isOrderBySetted()) ? orderBy.split(",") : new String[0];
	}

	/**
	 * 将排序方向转换为数组
	 */
	public String[] toArrayFromOrderType() {
		return (isOrderBySetted()) ? orderType.split(",") : new String[0];
	}

	/**
	 * 是否已设置排序字段, 无默认值.
	 */
	public boolean isOrderBySetted() {
		return (orderBy != null && orderBy.length() > 0) && (orderType != null && orderType.length() > 0);
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 设置查询对象时是否自动另外执行count查询获取总记录数.
	 * 
	 * @param 是否统计总数
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}

	/**
	 * 设置查询对象时是否自动另外执行count查询获取总记录数.
	 * 
	 * @param autoCount
	 *            是否统计总数
	 * @return this
	 */
	public Page<T> autoCount(final boolean autoCount) {
		setAutoCount(autoCount);
		return this;
	}

	/**
	 * 取得总记录数, 默认值为0.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 * 
	 * @param totalCount
	 *            总记录数
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 * 
	 * @param list
	 *            记录列表
	 */
	public void setResult(final List<T> list) {
		this.result = list;
	}

	public long getTotalPages() {
		 if( this.totalCount %  this.pageSize == 0){
			 return this.totalCount /  this.pageSize;
		  }else{
		    	 return (totalCount / pageSize) + 1;
		    }
		
	}

	
	
	
}
