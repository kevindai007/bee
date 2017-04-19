package cn.tongdun.bee.core.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import cn.tongdun.bee.core.support.Pagination;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import com.alibaba.druid.sql.PagerUtils;

/**
 * @author bsli@ustcinfo.com
 * @date 2013-1-29 下午2:07:39
 */
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
                                                     int page, int limit) {
		return this.queryPage(sql, page, limit, null);
	}

	public Pagination<Map<String, Object>> queryPage(String sql, 
			int page, int limit, Map<String, Object> paramMap) {
		String countSql = PagerUtils.count(sql, dbType);
		int offset = (page - 1) * limit;
		String limitSql = PagerUtils.limit(sql, dbType, offset, limit);
		
		long totalRecords = jdbcTemplate.queryForObject(countSql, paramMap, Long.class);
		List<Map<String, Object>> items = jdbcTemplate.queryForList(limitSql, paramMap);
		
		double totalPages = Math.ceil(totalRecords * 1d / limit);
		Pagination<Map<String, Object>> pagination = new Pagination<Map<String, Object>>((long)totalPages, offset, limit, totalRecords, items);
		return pagination;
	}

	public String getDbType() {
		return dbType;
	}
	
}
