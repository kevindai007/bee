package cn.tongdun.bee.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name="TEST_ACCOUNT")
@Data
public class Account {
	@Id
	@GeneratedValue(generator = "tableGenerator")     
    @GenericGenerator(name = "tableGenerator", strategy="increment")
	private Long id;
	private String name;
	private String email;
	private int age;
	private String cardNo;
	
	@OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)   
	@JoinColumn(name = "address_id")    
	private Address address;
}
