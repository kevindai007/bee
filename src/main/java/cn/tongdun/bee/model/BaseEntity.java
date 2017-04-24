package cn.tongdun.bee.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 *
 * @datetime 2010-8-12 下午05:15:55
 * @author libinsong1204@gmail.com
 */
@MappedSuperclass
public class BaseEntity implements Entity, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="CREATER", updatable=false)
	private String creater;
	@Column(name="MODIFIER")
	private String modifier;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="GMT_CREATED", updatable = false)
	private Date gmtCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GMT_MODIFIED")
	private Date gmtModified;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
}
