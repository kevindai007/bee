package cn.kevindai.bee;

import cn.kevindai.bee.enums.RoleEnum;
import cn.kevindai.bee.model.Account;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations={"classpath:dao-context.xml"})
public class ActiveRecordDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testFindByID() {
		Account account = new Account();
		account.setName("melin");
		account.setRole(RoleEnum.ADMIN);
		Long id = account.save();

		Account _account = Account.findById(id);

		assertEquals("melin", _account.getName());
		assertEquals(RoleEnum.ADMIN, _account.getRole());
	}
}
