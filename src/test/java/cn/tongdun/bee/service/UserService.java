package cn.tongdun.bee.service;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDao;
import cn.tongdun.bee.core.service.BaseServiceImpl;
import cn.tongdun.bee.model.User;
import cn.tongdun.bee.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 2017/2/26.
 */
public class UserService extends BaseServiceImpl<User, Long> {

    @Autowired
    private UserDao userDao;

    @Override
    public HibernateBaseDao getHibernateBaseDao() {
        return userDao;
    }

}
