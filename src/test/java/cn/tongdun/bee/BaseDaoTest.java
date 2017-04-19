package cn.tongdun.bee;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import cn.tongdun.bee.persistence.AccountDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import cn.tongdun.bee.model.Account;
import cn.tongdun.bee.model.Address;

@ContextConfiguration(locations={"classpath:dao-context.xml"})
public class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private AccountDao accountDao;
	
	@Test
	public void testFindByID() {
		Account account = new Account();
		account.setName("melin");
		Long id = accountDao.save(account);
		
		Account _account = accountDao.get(id);
		
		assertEquals("melin", _account.getName());
	}
	
	@Test
	public void testFindJoinEntity() {
		Account account = new Account();
		account.setName("melin");
		Address address = new Address();
		address.setName("hefei");
		account.setAddress(address);
		accountDao.save(account);
		
		List<Account> list = accountDao.findByNamedParam("address", "address.name", "hefei");
		assertEquals(1, list.size());
		assertEquals("melin", list.get(0).getName());
	}

	@Test
	public void testDeleteByHQL() {
		Account account = new Account();
		account.setName("test");
		Address address = new Address();
		address.setName("hangzhou");
		account.setAddress(address);
		accountDao.save(account);

		int count = accountDao.deleteOrUpdateByHQL("delete from Account where name = :name", "name", "test");
		assertEquals(1, count);
	}
}
