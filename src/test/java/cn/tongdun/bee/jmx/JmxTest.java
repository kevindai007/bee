/**
 * Copyright (c) 2012,USTC E-BUSINESS TECHNOLOGY CO.LTD All Rights Reserved.
 */

package cn.tongdun.bee.jmx;

import cn.tongdun.bee.core.jmx.SQLManager;
import cn.tongdun.bee.core.support.CustomSQLUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author libinsong1204@gmail.com
 * @date 2012-6-19 下午12:59:49
 */
public class JmxTest {

	@Test
	public void testRestSql() {
		SQLManager manager = new SQLManager();
		Assert.assertNotNull(manager.findSQL("testfreemarker"));
		Assert.assertEquals("select * from TEST_ACCOUNT WHERE 1=1",  CustomSQLUtil.get("testfreemarker", null).trim());
		
		manager.resetSql("testfreemarker", "freemarker", "sql1");
		
		Assert.assertEquals("sql1", manager.findSQL("testfreemarker"));
		
		Assert.assertEquals("sql1", CustomSQLUtil.get("testfreemarker", null));
	}
}
