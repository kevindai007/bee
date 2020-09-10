package cn.tongdun.bee.persistence;

import cn.tongdun.bee.model.Account;
import org.springframework.stereotype.Repository;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDaoImpl;

@Repository
public class AccountDao extends HibernateBaseDaoImpl<Account, Long> {

}
