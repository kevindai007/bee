/**
 * Copyright (c) 2012,USTC E-BUSINESS TECHNOLOGY CO.LTD All Rights Reserved.
 */

package cn.tongdun.bee.core.hibernate5;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * @author libinsong1204@gmail.com
 * @date 2012-7-26 下午1:04:48
 */
public interface HibernateCallback<T> {

	T doInHibernate(Session session) throws HibernateException, SQLException;

}

