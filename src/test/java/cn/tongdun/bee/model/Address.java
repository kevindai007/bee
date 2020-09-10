package cn.tongdun.bee.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="TEST_ADDRESS")
@Data
public class Address {
	@Id
	@GeneratedValue(generator = "tableGenerator")
    @GenericGenerator(name = "tableGenerator", strategy="increment")
	private Long id;
	private String name;
}
