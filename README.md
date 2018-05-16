基于hibernate5 封装的公共类，对于单表的CURD操作相比 mybatis 能够减少开发人员很大工作量

```xml
<dependency>
  <groupId>cn.tongdun.bee</groupId>
  <artifactId>bee</artifactId>
  <version>0.2.0-SNAPSHOT</version>
</dependency>
```

### 使用步骤，以用户实体为离
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
4. 参考测试用例：PaginationDaoTest

### 对于复杂的sql，建议使用Spring JdbcTemplate，为了避免SQL直接写在代码中 ，推荐写在配置文件中，具体使用方法：
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

### 定制sql分页查询, 建议使用 NamedParameterJdbcPager 类
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