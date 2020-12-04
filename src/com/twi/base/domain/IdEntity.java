package com.twi.base.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * 字符串类型的ID<br>
 * hibernate通过算法生成(32位长度)
 */
// JPA 基类的标识
@MappedSuperclass
public abstract class IdEntity extends Entity {
	@Id
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID_", length = 32)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstKeyColumnName() {
		return "id";
	}
}
