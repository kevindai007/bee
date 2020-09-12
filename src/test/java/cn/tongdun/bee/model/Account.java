package cn.tongdun.bee.model;

import javax.persistence.*;

import cn.tongdun.bee.enums.RoleEnum;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="TEST_ACCOUNT")
@Data
public class Account extends ActiveRecord {
	@Id
	@GeneratedValue(generator = "tableGenerator")
    @GenericGenerator(name = "tableGenerator", strategy="increment")
	private Long id;
	private String name;
	private String email;
	private int age;
	private String cardNo;

	@Column(name = "role")
	@Type(type = "cn.tongdun.bee.core.enums.StringValuedEnumType", parameters = {
			@Parameter(name = "enumClass", value = "cn.tongdun.bee.enums.RoleEnum")})
	private RoleEnum role;

	@OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;
}
