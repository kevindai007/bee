package cn.tongdun.bee.persistence;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDaoImpl;
import cn.tongdun.bee.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends HibernateBaseDaoImpl<User, Long> {

}
