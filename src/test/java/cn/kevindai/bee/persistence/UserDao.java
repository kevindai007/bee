package cn.kevindai.bee.persistence;

import cn.kevindai.bee.model.User;
import cn.kevindai.bee.core.hibernate5.HibernateBaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends HibernateBaseDaoImpl<User, Long> {

}
