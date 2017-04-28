package cn.tongdun.bee.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="TEST_USER")
@Data
public class User extends BaseEntity {
	private String name;
	private int age;
	private String cardNo;
	
	@OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)   
	@JoinColumn(name = "address_id")    
	private Address address;
}
