package cn.tongdun.bee.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by binsong.li on 2018/4/19 上午11:13
 */
@MappedSuperclass
@Data
public class TdBaseEntity implements IEntity, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="CREATER", updatable=false)
    private String creater;
    @Column(name="MODIFIER")
    private String modifier;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="gmt_create", updatable = false)
    private Date gmtCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="gmt_modify")
    private Date gmtModified;

    public Long getId() {
        return id;
    }
}
