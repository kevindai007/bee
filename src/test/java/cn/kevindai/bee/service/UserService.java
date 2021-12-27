package cn.kevindai.bee.service;

import cn.kevindai.bee.model.User;
import cn.kevindai.bee.core.hibernate5.HibernateBaseDao;
import cn.kevindai.bee.core.service.BaseServiceImpl;
import cn.kevindai.bee.persistence.UserDao;
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
