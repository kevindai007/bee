package cn.tongdun.bee.core.service;

import java.io.Serializable;
import java.util.List;

import cn.tongdun.bee.core.support.PaginationRequest;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.LobCreator;

import cn.tongdun.bee.core.support.Pagination;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @datetime 2010-8-9 上午09:14:46
 * @author libinsong1204@gmail.com
 */
public interface BaseService<T, ID extends Serializable> {
	
	public T getEntity(ID id);
	
	public ID insertEntity(T entity);
	
	public void updateEntity(T entity);
	
	public void createOrUpdate(T entity);
	
	public T deleteEntity(ID id);

	public List<T> findAllEntity();
	
	public T logicDeleteEntity(ID id);
	
	public void bulkDeleteEntity(ID[] ids);
	
	public List<T> loadEntities();
	
	public List<T> findByNamedParam(String propertyName, Object value);
	
	public List<T> findByNamedParam(String[] propertyNames, Object[] values);
	
	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order);
	
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order ... orders);
	
	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order);
	
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String propertyName, Object value, Order order);
	
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order order);

	public Pagination<T> findByNamedParamAndOrder(String propertyName, Object value, int page, int limit);

	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, int page, int limit);

	public Pagination<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, int page, int limit);

	public Pagination<T> findPage(int page, int limit);

	public Pagination<T> findPage(PaginationRequest<T> paginationRequest);
	
	public LobCreator getLobCreator();
}
