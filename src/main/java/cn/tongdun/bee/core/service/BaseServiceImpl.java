package cn.tongdun.bee.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDao;
import cn.tongdun.bee.core.support.PaginationRequest;
import cn.tongdun.bee.model.LoginUserDetails;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.LobCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import cn.tongdun.bee.core.support.Pagination;
import cn.tongdun.bee.model.BaseEntity;

/**
 * 服务层，公共类
 * 
 * @datetime 2010-8-9 上午09:15:19
 * @author libinsong1204@gmail.com
 */
abstract public class BaseServiceImpl<T extends BaseEntity, ID extends Serializable> implements BaseService<T, ID>, InitializingBean {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected Class<T> entityClass;
	
	protected boolean isAssignableBaseEntity = true;

	abstract public HibernateBaseDao<T, ID> getHibernateBaseDao();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			entityClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
			isAssignableBaseEntity = ClassUtils.isAssignable(BaseEntity.class, entityClass);
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public T getEntity(ID id) {
		return this.getHibernateBaseDao().get(id);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findAllEntity() {
		return this.getHibernateBaseDao().loadAll();
	}
	
	@Transactional
	@Override
	public ID insertEntity(T entity) {
		setCreaterAndTime(entity);
		return this.getHibernateBaseDao().save(entity);
	}
	
	@Transactional
	@Override
	public void updateEntity(T entity) {
		setModifierAndTime(entity);
		this.getHibernateBaseDao().update(entity);
	}
	
	@Transactional
	@Override
	public T deleteEntity(ID id) {
		return this.getHibernateBaseDao().delete(id);
	}
	
	@Transactional
	@Override
	public T logicDeleteEntity(ID id) {
		T entity = this.getEntity(id);
		if(entity != null && entity instanceof BaseEntity) {
			BaseEntity be = (BaseEntity)entity;
			//be.setDelFlag(1);
			this.updateEntity((T) be);
		}
		
		return entity;
	}
	
	@Transactional
	@Override
	public void bulkDeleteEntity(ID[] ids) {
		for(ID id : ids) {
			this.deleteEntity(id);
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> loadEntities() {
		return this.getHibernateBaseDao().loadAll();
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParam(String propertyName, Object value) {
		return this.getHibernateBaseDao().findByNamedParam(propertyName, value);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParam(String[] propertyNames, Object[] values) {
		return this.getHibernateBaseDao().findByNamedParam(propertyNames, values);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(propertyName, value, order);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order ... orders) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(propertyNames, values, orders);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order) {
		return this.findByNamedParamAndOrder(new String[]{joinEntity}, propertyName, value, order);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String propertyName, Object value, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(joinEntitys, new String[]{propertyName}, 
				new Object[]{value}, new Order[]{order});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(joinEntitys, propertyNames, values, new Order[]{order});
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findByNamedParamAndOrder(String propertyName, Object value, int page, int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParam(propertyName, value, offset, limit);
	}

	@Transactional(readOnly=true)
	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, int page, int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParam(propertyNames, values, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, int page, int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParamAndOrder(propertyNames, values, orders, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPage(int page, int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByExample(offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPage(PaginationRequest<T> paginationRequest) {
		return this.getHibernateBaseDao().findPage(paginationRequest);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParam(String joinEntity, String propertyName, Object value,
											  final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParam(joinEntity, propertyName, value, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParam(String propertyName, Object value, final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParam(propertyName, value, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String propertyName, Object value, Order order,
													  final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParamAndOrder(propertyName, value, order, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParam(String[] propertyNames, Object[] values, final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParam(propertyNames, values, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders,
													  final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParamAndOrder(propertyNames, values, orders, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values,
													  final Order[] orders, final int page, final int limit) {
		int offset = (page - 1) * limit;
		return this.getHibernateBaseDao().findPageByNamedParamAndOrder(joinEntitys, propertyNames, values, orders, offset, limit);
	}

	@Transactional(readOnly=true)
	@Override
	public Long queryCount(String propertyName, Object value) {
		return this.getHibernateBaseDao().findCountByNamedParam(propertyName, value);
	}

	@Transactional(readOnly=true)
	@Override
	public Long queryCount(String[] propertyNames, Object[] values) {
		return this.getHibernateBaseDao().findCountByNamedParam(propertyNames, values);
	}
	
	public LobCreator getLobCreator() {
		return this.getHibernateBaseDao().getLobCreator();
	}

	private void setCreaterAndTime(T entity) {
		LoginUserDetails userDetails = (LoginUserDetails)SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if(userDetails == null) {
			logger.warn("SecurityContex access to information is empty, please login system.");
		} else {
			String name = userDetails.getCnName() + "#" + userDetails.getUsername();
			entity.setCreater(name);
			entity.setGmtCreated(new Date());
			entity.setModifier(name);
			entity.setGmtModified(new Date());
		}
	}

	private void setModifierAndTime(T entity) {
		LoginUserDetails userDetails = (LoginUserDetails)SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if(userDetails == null) {
			logger.warn("SecurityContex access to information is empty, please login system.");
		} else {
			String name = userDetails.getCnName() + "#" + userDetails.getUsername();
			entity.setModifier(name);
			entity.setGmtModified(new Date());
		}
	}
}
