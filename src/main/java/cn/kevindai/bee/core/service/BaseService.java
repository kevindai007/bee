package cn.kevindai.bee.core.service;

import java.io.Serializable;
import java.util.List;

import cn.kevindai.bee.core.support.Pagination;
import cn.kevindai.bee.core.support.PaginationRequest;
import cn.kevindai.bee.model.IEntity;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.LobCreator;

/**
 *
 * @datetime 2010-8-9 上午09:14:46
 * @author admin@gmail.com
 */
public interface BaseService<T extends IEntity, ID extends Serializable> {

	public T getEntity(ID id);

	public ID insertEntity(T entity);

	public void updateEntity(T entity);

	public T deleteEntity(ID id);

	public List<T> findAllEntity();

	public T logicDeleteEntity(ID id);

	public void bulkDeleteEntity(ID[] ids);

	public List<T> loadEntities();

	public List<T> findByNamedParam(String propertyName, Object value);

	public List<T> findByNamedParam(String[] propertyNames, Object[] values);

	public List<T> findByNamedParam(List<String> propertyNames, List<Object> values);

	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order);

	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order ... orders);

	public List<T> findByNamedParamAndOrder(List<String> propertyNames, List<Object> values, Order ... orders);

	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order);

	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String propertyName, Object value, Order order);

	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order order);

	public Pagination<T> findByNamedParamAndOrder(String propertyName, Object value, Order order, int page, int limit);

	public Pagination<T> findPage(int page, int limit);

	public Pagination<T> findPageAndOrder(Order[] orders, int page, int limit);

	public Pagination<T> findPage(PaginationRequest<T> paginationRequest);

	public Pagination<T> findPageByNamedParam(String joinEntity, String propertyName, Object value, final int page, final int limit);

	public Pagination<T> findPageByNamedParam(String propertyName, Object value, final int page, final int limit);

	public Pagination<T> findPageByNamedParamAndOrder(String propertyName, Object value, Order order, final int page, final int limit);

	public Pagination<T> findPageByNamedParam(String[] propertyNames, Object[] values, final int page, final int limit);

	public Pagination<T> findPageByNamedParam(List<String> propertyNames, List<Object> values, final int page, final int limit);

	public Pagination<T> findPageByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, final int page, final int limit);

	public Pagination<T> findPageByNamedParamAndOrder(List<String> propertyNames, List<Object> values, List<Order> orders, final int page, final int limit);

	public Pagination<T> findPageByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values,
													  final Order[] orders, final int page, final int limit);

	public List<T> findByNamedParam(String[] propertyNames, Object[] values, final int offset, final int limit);

	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, final int offset, final int limit);

	public Long queryCount(String propertyName, Object value);

	public Long queryCount(String[] propertyNames, Object[] values);

	public Integer deleteOrUpdateByHQL(final String hql, final String paramName, final Object value);

	public Integer deleteOrUpdateByHQL(final String hql, final String[] paramNames, final Object[] values);


	public LobCreator getLobCreator();
}
