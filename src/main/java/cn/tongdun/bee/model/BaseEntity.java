package cn.tongdun.bee.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 *
 * @datetime 2010-8-12 下午05:15:55
 * @author admin@gmail.com
 */
@MappedSuperclass
@Data
public class BaseEntity implements IEntity, Serializable {
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
}
