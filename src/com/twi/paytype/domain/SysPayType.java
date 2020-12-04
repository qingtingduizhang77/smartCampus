package com.twi.paytype.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 缴费类型
 * @author zhengjc
 *
 */
@Entity
@Table(name = "sys_pay_type")
public class SysPayType extends IdEntity implements Serializable{

	private static final long serialVersionUID = 8304027089518133561L;

	@Column(name = "name_")
	private String name;//显示名称
	
	@Column(name = "code_")
//	private Integer code;//编号
	private String code;//'收费项目代码
	
	@Column(name = "sys_url")
	private String sysUrl;//后台地址
	
	@Column(name = "fron_url")
	private String fronUrl;//前端地址
	
	@Column(name = "from_code")
	private int fromCode;//缴费来源编码1：本地导入2：接口获取数据3：用户自输金额4：自定义缴费
	
	@Column(name = "from_name")
	private String fromName;//缴费来源名称
	
	@Column(name = "type_code")
	private int typeCode;//缴费形式编码1：一次性缴费2：分多次缴费
	
	@Column(name = "type_name")
	private String typeName;//缴费形式名称
	
	@Column(name = "pay_menu_ID")
	private String payMenuId;//缴费菜单ID
	
	@Column(name = "pay_menu_name")
	private String payMenuName;//缴费菜单名称
	
	@Column(name = "remark_")
	private String remark;//备注
	
	@Column(name = "state_")
	private int state;//状态1:正常2禁用
	
	@Column(name = "creater_date")
	private Date createDate;//创建时间
	
	@Column(name = "org_id")
	private String orgId;//学校id

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSysUrl() {
		return sysUrl;
	}

	public void setSysUrl(String sysUrl) {
		this.sysUrl = sysUrl;
	}

	public String getFronUrl() {
		return fronUrl;
	}

	public void setFronUrl(String fronUrl) {
		this.fronUrl = fronUrl;
	}

	public int getFromCode() {
		return fromCode;
	}

	public void setFromCode(int fromCode) {
		this.fromCode = fromCode;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public int getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(int typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getPayMenuId() {
		return payMenuId;
	}

	public void setPayMenuId(String payMenuId) {
		this.payMenuId = payMenuId;
	}

	public String getPayMenuName() {
		return payMenuName;
	}

	public void setPayMenuName(String payMenuName) {
		this.payMenuName = payMenuName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	
	
	
}
