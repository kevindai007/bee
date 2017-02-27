package cn.tongdun.bee.persistence;

import org.springframework.stereotype.Repository;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDaoImpl;
import cn.tongdun.bee.model.Account;

@Repository
public class AccountDao extends HibernateBaseDaoImpl<Account, Long> {

}
