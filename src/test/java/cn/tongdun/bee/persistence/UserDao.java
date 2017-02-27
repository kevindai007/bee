package cn.tongdun.bee.persistence;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDaoImpl;
import org.springframework.stereotype.Repository;

import cn.tongdun.bee.model.User;

@Repository
public class UserDao extends HibernateBaseDaoImpl<User, Long> {

}
