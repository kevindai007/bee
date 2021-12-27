package cn.kevindai.bee.persistence;

import cn.kevindai.bee.model.Account;
import org.springframework.stereotype.Repository;

import cn.kevindai.bee.core.hibernate5.HibernateBaseDaoImpl;

@Repository
public class AccountDao extends HibernateBaseDaoImpl<Account, Long> {

}
