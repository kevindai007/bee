package cn.tongdun.bee;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.tongdun.bee.model.Account;
import cn.tongdun.bee.model.Address;
import cn.tongdun.bee.persistence.AccountDao;
import cn.tongdun.bee.core.support.CustomSQLUtil;
import cn.tongdun.bee.core.support.Pagination;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:dao-context.xml"})
public class PaginationDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private AccountDao accountDao;

	@Before
	public void createData() {
		for(int i = 1; i <= 53; i++) {
			Account account = new Account();
			account.setName("test" + i);
			account.setEmail("email" + i);
			account.setAge(i);

			Address address = new Address();
			address.setName("address" + i);
			account.setAddress(address);
			accountDao.save(account);
		}
	}

	@Test
	public void testPagination() {
		Pagination<Account> pagination = accountDao.findPageByExample(0, 10);

		assertEquals(53, pagination.getTotalRecords());
		assertEquals(6, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}

	@Test
	public void testPaginationLikeCondition() {
		Pagination<Account> pagination = accountDao.findPageByNamedParam("name", Restrictions.like("name", "test1%"), 10, 10);

		assertEquals(11, pagination.getTotalRecords());
		assertEquals(2, pagination.getTotalPages());
		assertEquals(1, pagination.getResult().size());
	}

	@Test
	public void testPaginationInConditionForArray() {
		String[] names = new String[]{"test1", "test2", "test3"};
		Pagination<Account> pagination = accountDao.findPageByNamedParam("name", names, 0, 10);

		assertEquals(3, pagination.getTotalRecords());
		assertEquals(1, pagination.getTotalPages());
		assertEquals(3, pagination.getResult().size());

		Integer[] ages = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
		pagination = accountDao.findPageByNamedParam("age", ages, 0, 10);

		assertEquals(13, pagination.getTotalRecords());
		assertEquals(2, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}

	@Test
	public void testPaginationInConditionForCollection() {
		List<String> names = new ArrayList<String>();
		names.add("test1");
		names.add("test2");
		names.add("test3");

		Pagination<Account> pagination = accountDao.findPageByNamedParam("name", names, 0, 10);

		assertEquals(3, pagination.getTotalRecords());
		assertEquals(1, pagination.getTotalPages());
		assertEquals(3, pagination.getResult().size());
	}

	@Test
	public void testPaginationSimpleExpression() {
		SimpleExpression simpleExpression = Restrictions.le("age", 20);

		Pagination<Account> pagination = accountDao.findPageByNamedParam("name", simpleExpression, 0, 10);

		assertEquals(20, pagination.getTotalRecords());
		assertEquals(2, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}

	@Test
	public void testPaginationBetweenExpression() {
		Criterion betweenExpression = Restrictions.between("age", 10, 30);

		Pagination<Account> pagination = accountDao.findPageByNamedParam("name", betweenExpression, 0, 10);

		assertEquals(21, pagination.getTotalRecords());
		assertEquals(3, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}

	/**
	 * 测试or条件
	 */
	@Test
	public void testPaginationOrLike() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", "test%");
		map.put("email", "email%");
		Pagination<Account> pagination = accountDao.findPageByNamedParam("like", map, 0, 10);

		assertEquals(53, pagination.getTotalRecords());
		assertEquals(6, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}

	/**
	 * 测试or条件
	 */
	@Test
	public void testPaginationOrEqual() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", "test1");
		map.put("email", "email2");
		Pagination<Account> pagination = accountDao.findPageByNamedParam("equal", map, 0, 10);

		assertEquals(2, pagination.getTotalRecords());
		assertEquals(1, pagination.getTotalPages());
		assertEquals(2, pagination.getResult().size());
	}

	@Test
	public void testPaginationJoinEntity() {
		Pagination<Account> pagination = accountDao.findPageByNamedParam("address", "address.name", "address2", 0, 10);

		assertEquals(1, pagination.getTotalRecords());
		assertEquals(1, pagination.getTotalPages());
		assertEquals(1, pagination.getResult().size());
	}

	@Test
	public void testPaginationByDefineSql() {
		String rowsql = CustomSQLUtil.get("rowsql");
		String countsql = CustomSQLUtil.get("countsql");
		Pagination<Object> pagination = accountDao.findPageByHQL(rowsql, countsql, 0, 10);

		assertEquals(53, pagination.getTotalRecords());
		assertEquals(6, pagination.getTotalPages());
		assertEquals(10, pagination.getResult().size());
	}
}
