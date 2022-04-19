package com.jhopesoft.framework.dao.entity.common;
// Generated 2022-03-25 16:21:56 by Quick build System

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * 国民经济行业门类(PubIndustryCategory) generated by Quick Build System 
 *
 * 了解快速架构系统 https://github.com/jfok1972
 *
 * @author 蒋锋 jfok1972@qq.com
 *
*/
@Entity
@DynamicUpdate
@Table(name = "pub_industry_category")
public class PubIndustryCategory implements Serializable {

	/** 门类代码 */
	private String categoryId;
	/** 行业门类名称 */
	private String categoryName;
	private Set<PubIndustryClass> pubIndustryClasss = new LinkedHashSet<PubIndustryClass>(0);

	public PubIndustryCategory() {
	}

	@Id

	@Column(name = "category_id_", unique = true, nullable = false, length = 1)
	public String getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "category_name_", nullable = false, length = 50)
	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pubIndustryCategory")
	public Set<PubIndustryClass> getPubIndustryClasss() {
		return this.pubIndustryClasss;
	}

	public void setPubIndustryClasss(Set<PubIndustryClass> pubIndustryClasss) {
		this.pubIndustryClasss = pubIndustryClasss;
	}

}