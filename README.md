基于Spring + Hibernate + JdbcTemplate + Freemark封装的DAO层，简单的CURD使用Hibernate，join等复杂sql可使用freemarker 组装sql，可以省去大半的sql，方便简单快捷。

Base Service和Dao封装了大量的方法，可以省去了大量的代码，基本CURD操作基本需要自定方法，datacompute 项目基于bee开发dao层代码。详细内容请参考:
- [BaseServiceImpl](https://gitlab.fraudmetrix.cn/internal/bee/blob/master/src/main/java/cn/tongdun/bee/core/service/BaseServiceImpl.java)
- [HibernateBaseDaoImpl](https://gitlab.fraudmetrix.cn/internal/bee/blob/master/src/main/java/cn/tongdun/bee/core/hibernate5/HibernateBaseDaoImpl.java)

可以结合这个项目简化hibernate 操作：https://quarkus.io/guides/hibernate-orm-panache

### 引用：所有依靠代码生成的应用都是弟弟，动态字节码才是王道。基于mybatis写的项目终将变成难以维护。所有把面向对象变成面相过程的设计规范、框架都是技术发展的倒退

### 一、使用步骤，以用户实体为离
1. 定义User Entity，继承BaseEntity
   ```java
   @Entity
   @Table(name="TEST_USER")
   public class User extends BaseEntity {
   	    private String name;
   	    private int age;
        
        ...省略getter 和 setter
    }
   ```
2. 定义DAO类，定义接口实际意义不大，不推荐定义接口
    ```java
    @Repository
    public class UserDao extends HibernateBaseDaoImpl<User, Long> {
    
    }
    ```
3. 定义Service类
    ```java
    @Service
    public class UserService extends BaseServiceImpl<User, Long> {
    
        @Autowired
        private UserDao userDao;
    
        @Override
        public HibernateBaseDao getHibernateBaseDao() {
            return userDao;
        }
    
    }
    ```
4. DataCcompute中使用例子
```java
//案例一
Criterion tableName = Restrictions.like("tableName", searchValue, MatchMode.ANYWHERE);
Criterion modifier = Restrictions.like("modifier", searchValue, MatchMode.ANYWHERE);
List<TableEntity> tableList = tableService.findByNamedParamAndOrder(new String[]{"databaseName", "tableName"},
        new Object[]{projectCode, Restrictions.or(tableName, modifier)}, Order.asc("tableName"));

//案例二
Criterion name = Restrictions.like("name", searchValue, MatchMode.ANYWHERE);
Criterion modifier = Restrictions.like("modifier", searchValue, MatchMode.ANYWHERE);

String[] paramKeys = new String[]{"projectCode", "trash", "current", "name"};
Object[] paramValus = new Object[]{projectCode, trash, current, Restrictions.or(name, modifier)};
if ("job".equals(type) && "yes".equals(displayOwnerFolder)) {
    paramKeys = ArrayUtils.add(paramKeys, "owner");
    paramValus = ArrayUtils.add(paramValus, AuthUtil.getFullName());
}

List<JobEntity> jobEntityList = jobService.findByNamedParamAndOrder(paramKeys, paramValus, Order.asc("name"));

//案例三
if (StringUtils.isNotEmpty(startTime)) {
    params.add("runStart");
    SimpleExpression contentCri = Restrictions.ge("runStart", DateUtils.convertDate(startTime));
    values.add(Restrictions.and(contentCri));
}

if (StringUtils.isNotEmpty(endTime)) {
    params.add("runStart");
    SimpleExpression contentCri = Restrictions.le("runStart", DateUtils.convertDate(endTime));
    values.add(Restrictions.and(contentCri));
}

if (status != null) {
    params.add("status");
    values.add(status);
}

Order order1 = Order.desc("gmtModified");
if (StringUtils.isNotEmpty(sort)) {
    if ("asc".equals(order)) {
        order1 = Order.asc(sort);
    } else {
        order1 = Order.desc(sort);
    }
}

return jobInstanceService.findPageByNamedParamAndOrder(params.toArray(new String[]{}), values.toArray(),
        new Order[]{order1}, page, rows);

```
  
5. 参考测试用例：PaginationDaoTest

### 二、对于复杂的sql，建议使用Spring JdbcTemplate，为了避免SQL直接写在代码中 ，推荐写在配置文件中，具体使用方法：
1. 在resources目录下创建目录custom-sql，custom-sql 目录中创建xml文件，xml文件定义sql语句，一个xml文件可以定义多个sql语句，建议一个DAO类对应一个xml文件，例如：user-sql.xml
    ```xml
    <sqls>
       <!--
       id: 必须全局唯一，建议取名 DAO类名.方法名称
       sqlTye: SQL或HQL
       tempateType：simple或freeMarker，如果sql没有动态参数，选择simple. 选择freeMarker，sql语句当着freeMarker模板，可以动态组装sql。
       -->
       <sql id="rowsql" sqlType="SQL" tempateType="simple">  
           <![CDATA[  
               select * from TEST_ACCOUNT
           ]]>  
       </sql>
        
       <sql id="testfreemarker" sqlType="SQL" tempateType="freeMarker">  
           <![CDATA[  
               select * from TEST_ACCOUNT WHERE 1=1 <#if name??>AND name=:name</#if> 
           ]]>  
       </sql>  
    </sqls>
    ```
2. CustomSQL 注册为spring bean
3. 代码中使用 CustomSQLUtil.get(String id, Map<String, Object> models) 获取sql。

### 三、定制sql分页查询, 建议使用 NamedParameterJdbcPager 类
```java
public class NamedParameterJdbcPager {
	
	/**
	 * dbType: 数据库类型，具体值请参考：com.alibaba.druid.util.JdbcConstants
	 */
	private final String dbType;
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public NamedParameterJdbcPager(DataSource dataSource, String dbType) {
		Assert.isNull(dbType, "dbType cannot be empty");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dbType = dbType;
	}
	
	public Pagination<Map<String, Object>> queryPage(String sql,
                                                     int offset, int limit) {
		return this.queryPage(sql, offset, limit, null);
	}

	public Pagination<Map<String, Object>> queryPage(String sql, 
			int offset, int limit, Map<String, Object> paramMap) {
			
		// 根据不同数据库，生成对应分析SQL
		String countSql = PagerUtils.count(sql, dbType);
		String limitSql = PagerUtils.limit(sql, dbType, offset, limit);
		
		long totalRecords = jdbcTemplate.queryForObject(countSql, paramMap, Long.class);
		List<Map<String, Object>> items = jdbcTemplate.queryForList(limitSql, paramMap);
		
		double totalPages = Math.ceil(totalRecords * 1d / limit);
		Pagination<Map<String, Object>> page = new Pagination<Map<String, Object>>((long)totalPages, offset, limit, totalRecords, items);
		return page;
	}

	public String getDbType() {
		return dbType;
	}
	
}
```

### 四、Entity 字段定义enum，解决spring mvc 和 hibernate 支持enum，Example: 
spring mvc Formatter 支持 Enum
```java
@Override
public void addFormatters(FormatterRegistry registry) {
    registry.addConverterFactory(new IntegerToEnumConverterFactory());
    registry.addConverterFactory(new StringToEnumConverterFactory());
}

```

hibernate 
```java
@JsonSerialize(using = JacksonEnumStringSerializer.class)
// @JsonSerialize(using = JacksonEnumIntegerSerializer.class)
public enum RoleEnum implements BaseStringEnum { //如果enum value为int 请使用 BaseIntegerEnum
    ADMIN("admin", "管理员"),
    USER("user", "普通用户");

    private String value;

    private String desc;

    RoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}

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

	@Column(name = "role")
	@Type(type = "com.gitee.bee.core.enums.StringValuedEnumType", parameters = {
			@Parameter(name = "enumClass", value = "com.gitee.bee.enums.RoleEnum")})
	private RoleEnum role;

	@OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;
}

@Test
public void testFindByID() {
    Account account = new Account();
    account.setName("melin");
    account.setRole(RoleEnum.ADMIN);
    Long id = accountDao.save(account);

    Account _account = accountDao.get(id);

    assertEquals("melin", _account.getName());
    assertEquals(RoleEnum.ADMIN, _account.getRole());
}

```


### 五、基于spring security扩展的账号管理

```sql
-- 确保用户表有email 字段
ALTER TABLE user
ADD COLUMN `email` VARCHAR(256) NULL DEFAULT null COMMENT '用户邮箱' AFTER `status`,
ADD COLUMN `sec_enabled` TINYINT(1) NULL DEFAULT 1 COMMENT '表示是否可用，0 无效，1 有效' AFTER `email`,
ADD COLUMN `sec_password_expired_date` DATETIME NULL DEFAULT null COMMENT '表示是否可用，0 过期，1 未过期' AFTER `sec_enabled`,
ADD COLUMN `sec_account_expired_date` DATETIME NULL DEFAULT NULL COMMENT '账号过期时间' AFTER `sec_account_non_expired`,
ADD COLUMN `sec_locked_date` DATETIME NULL DEFAULT NULL COMMENT '账号锁定到期时间' AFTER `sec_account_expired_date`,
ADD COLUMN `sec_non_update_pw` TINYINT(1) NULL DEFAULT 1 COMMENT '系统分配默认账号，或者重置密码，首次登录需要强制修改密码， 0 需要更新密码，1 不需要更新密码，' AFTER `sec_locked_date`,
ADD COLUMN `sec_attempts` INT NULL DEFAULT 0 COMMENT '密码尝试登录次数, 找过一定次数，锁定一段时间' AFTER `sec_non_update_pw`,
ADD COLUMN `sec_login_date` DATETIME NULL DEFAULT NULL COMMENT '最近登录时间' AFTER `sec_attempts`,
ADD COLUMN `sec_confirm` TINYINT(1) NULL DEFAULT 0 COMMENT '账号是否验证通过，0 未验证，1 验证' AFTER `sec_login_date`,
ADD COLUMN `sec_comfirm_token` VARCHAR(256) NULL DEFAULT NULL COMMENT '账号验证token' AFTER `sec_non_confirm`,
ADD COLUMN `sec_comfirm_expired_date` DATETIME NULL DEFAULT NULL COMMENT '账号验证token过期时间' AFTER `sec_comfirm_token`;

```

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 静态资源不走spring security 过滤器链，效率更高，
     * 参考：https://xie.infoq.cn/article/7ec9773eb2f9f493cadd18855
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.js", "/**/*.css", "/**/*.png", "/datainsight.ico", "/ok.htm");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        http.cors()
                .and()
                .authorizeRequests()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/changePassword").permitAll()
                .antMatchers("/user/changePassword").permitAll()
                .antMatchers("/version").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin().loginPage("/login")
                .failureHandler(new UserNameAuthenticationFailureHandler("/login", "/changePassword"))
                .successHandler(authenticationSuccessHandler())

                .and()
                .csrf().disable()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/login"))

                .and()
                .logout().logoutSuccessUrl("/")
                .permitAll()

                .and()
                .rememberMe()
                .userDetailsService(new LdapUserDetailsService())
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(15 * 24 * 60 * 60); //15天
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("TDDISESSIONID");
        serializer.setCookiePath("/");
        serializer.setCookieMaxAge(604800);
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            PasswordEncoder passwordEncoder) throws Exception {
        auth.authenticationProvider(authenticationProvider(passwordEncoder));
    }

    @Bean
    public BeeUserDetailsService userDetailsService() {
        BeeUserDetailsService userDetailsService = new BeeUserDetailsService();
        userDetailsService.setUserTableName("di_user");
        userDetailsService.setUserIdColumnName("username");
        userDetailsService.setCnNameColumnName("name");
        userDetailsService.setJdbcTemplate(jdbcTemplate);
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        BeeAuthenticationProvider provider = new BeeAuthenticationProvider();
        provider.setMaxLoginFailures(6);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        BeeAuthenticationSuccessHandler handler = new BeeAuthenticationSuccessHandler(userDetailsService());
        handler.setDefaultTargetUrl("/");
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }
}
```
