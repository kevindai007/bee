/**
 * Copyright (c) 2011, SuZhou USTC Star Information Technology CO.LTD
 * All Rights Reserved.
 */

package cn.tongdun.bee.core.support;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author   admin@gmail.com
 * @Date	 2011-6-14 下午01:41:33
 */
public class CustomSQLUtil {
	private static Logger logger = LoggerFactory.getLogger(CustomSQLUtil.class);

	private final static CustomSQLUtil _instance = new CustomSQLUtil();

	private AtomicBoolean init = new AtomicBoolean(false);

	private CustomSQL _customSQL;

	private CustomSQLUtil() {
		try {
			if(init.compareAndSet(false, true))
				_customSQL = CustomSQL.getInstance();
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String get(String id) {
		return _instance._customSQL.get(id);
	}

	public static String get(String id, Map<String, Object> models) {
		return _instance._customSQL.get(id, models);
	}
}
