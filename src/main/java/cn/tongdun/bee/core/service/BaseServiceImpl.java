package cn.tongdun.bee.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import cn.tongdun.bee.core.hibernate5.HibernateBaseDao;
import cn.tongdun.bee.core.support.PaginationRequest;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.LobCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
abstract public class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID>, InitializingBean {
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
	public T getEntity(ID id) {
		return this.getHibernateBaseDao().get(id);
	}
	
	@Transactional(readOnly=true)
	public List<T> findAllEntity() {
		return this.getHibernateBaseDao().loadAll();
	}
	
	@Transactional
	public ID insertEntity(T entity) {
		return this.getHibernateBaseDao().save(entity);
	}
	
	@Transactional
	public void updateEntity(T entity) {
		this.getHibernateBaseDao().update(entity);
	}
	
	@Transactional
	public void createOrUpdate(T entity) {
		this.getHibernateBaseDao().saveOrUpdate(entity);
	}
	
	@Transactional
	public T deleteEntity(ID id) {
		return this.getHibernateBaseDao().delete(id);
	}
	
	@Transactional
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
	public void bulkDeleteEntity(ID[] ids) {
		for(ID id : ids) {
			this.deleteEntity(id);
		}
	}
	
	@Transactional(readOnly=true)
	public List<T> loadEntities() {
		return this.getHibernateBaseDao().loadAll();
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParam(String propertyName, Object value) {
		return this.getHibernateBaseDao().findByNamedParam(propertyName, value);
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParam(String[] propertyNames, Object[] values) {
		return this.getHibernateBaseDao().findByNamedParam(propertyNames, values);
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(propertyName, value, order);
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order ... orders) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(propertyNames, values, orders);
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order) {
		return this.findByNamedParamAndOrder(new String[]{joinEntity}, propertyName, value, order);
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String propertyName, Object value, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(joinEntitys, new String[]{propertyName}, 
				new Object[]{value}, new Order[]{order});
	}
	
	@Transactional(readOnly=true)
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order order) {
		return this.getHibernateBaseDao().findByNamedParamAndOrder(joinEntitys, propertyNames, values, new Order[]{order});
	}

	@Transactional(readOnly=true)
	public Pagination<T> findByNamedParamAndOrder(String propertyName, Object value, int offset, int limit) {
		return this.getHibernateBaseDao().findPageByNamedParam(propertyName, value, offset, limit);
	}

	@Transactional(readOnly=true)
	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, int offset, int limit) {
		return this.getHibernateBaseDao().findPageByNamedParam(propertyNames, values, offset, limit);
	}

	@Transactional(readOnly=true)
	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, int offset, int limit) {
		return this.getHibernateBaseDao().findPageByNamedParamAndOrder(propertyNames, values, orders, offset, limit);
	}
	
	@Transactional(readOnly=true)
	public Pagination<T> findPage(PaginationRequest<T> paginationRequest) {
		return this.getHibernateBaseDao().findPage(paginationRequest);
	}
	
	public LobCreator getLobCreator() {
		return this.getHibernateBaseDao().getLobCreator();
	}
}
