package cn.kevindai.bee.core.hibernate5;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.kevindai.bee.core.support.Pagination;
import cn.kevindai.bee.core.support.PaginationRequest;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ReplicationMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.LobCreator;

/**
 * 提供了常用增删改查(CRUD)功能的DAO基础接口
 *
 * @author admin@gmail.com
 */
@SuppressWarnings("rawtypes")
public interface HibernateBaseDao<T, ID extends Serializable> {

	public LobCreator getLobCreator();

	// -------------------------------------------------------------------------
	// Convenience methods for loading individual objects
	// -------------------------------------------------------------------------
	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#get(Class, Serializable)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#get(Class, Serializable)
	 */
	public T get(ID id);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#get(Class, Serializable, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#get(Class, Serializable, LockMode)
	 */
	public T get(final ID id, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#load(Class, Serializable)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#load(Class, Serializable)
	 */
	public T load(ID id);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#load(Class, Serializable, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#load(Class, Serializable, LockMode)
	 */
	public T load(final ID id, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#loadAll(Class);
	 * @see org.springframework.orm.hibernate5.HibernateOperations#loadAll(Class)
	 */
	public List<T> loadAll();

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#load(Object, Serializable)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#load(Object, Serializable)
	 */
	public void load(final T entity, final ID id);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#refresh(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#refresh(Object)
	 */
	public void refresh(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#refresh(Object, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#refresh(Object, LockMode)
	 */
	public void refresh(final T entity, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#contains(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#contains(Object)
	 */
	public boolean contains(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#evict(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#evict(Object)
	 */
	public void evict(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#initialize(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#initialize(Object)
	 */
	public void initialize(T proxy);

	/**
	 * Get the identifier of entity
	 */
	public Serializable getIdentifierObject(T entity);

	// -------------------------------------------------------------------------
	// Convenience methods for storing individual objects
	// -------------------------------------------------------------------------

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#lock(Object, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#load(Object, Serializable)
	 */
	public void lock(final T entity, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#save(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#save(Object)
	 */
	public ID save(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#update(Object)
	 */
	public void update(T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(Object, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#update(Object, LockMode)
	 */
	public void update(final T entity, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#saveOrUpdate(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#saveOrUpdate(Object)
	 */
	public void saveOrUpdate(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#replicate(Object, ReplicationMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#replicate(Object, ReplicationMode)
	 */
	public void replicate(final T entity, final ReplicationMode replicationMode);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#persist(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#persist(Object)
	 */
	public void persist(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#merge(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#merge(Object)
	 */
	public T merge(final T entity);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#delete(Object)
	 */
	public void delete(T entity);

	/**
	 * Delete the given identifier.
	 * @param id the identifier to delete
	 * @throws org.springframework.dao.DataAccessException in case of Hibernate errors
	 * @see org.hibernate.Session#delete(Object)
	 */
	public T delete(ID id);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(Object, LockMode)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#delete(Object, LockMode)
	 */
	public void delete(final T entity, final LockOptions lockOption);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#deleteAll(Collection)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#deleteAll(Collection)
	 */
	public void deleteAll(final Collection<T> entities);

	/**
	 * hsql 执行删除数据
	 *
	 * @param hql
	 * @param paramName
	 * @param value
	 * @return
	 */
	public Integer deleteOrUpdateByHQL(final String hql, final String paramName, final Object value);

	/**
	 * hsql 执行删除数据
	 *
	 * @param hql
	 * @param paramNames
	 * @param values
	 * @return
	 */
	public Integer deleteOrUpdateByHQL(final String hql, final String[] paramNames, final Object[] values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#flush()
	 * @see org.springframework.orm.hibernate5.HibernateOperations#flush()
	 */
	public void flush();

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#clear()
	 * @see org.springframework.orm.hibernate5.HibernateOperations#clear()
	 */
	public void clear();

	// -------------------------------------------------------------------------
	// Convenience finder methods for HQL strings
	// -------------------------------------------------------------------------
	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#find(String, Object...)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#find(String, Object...)
	 */
	public List findByHQL(final String queryString, final Object... values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedParam(String, String, Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedParam(String, String, Object)
	 */
	public List findByHQLNamedParam(String queryString, String paramName, Object value);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedParam(String, String[], Object[])
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedParam(String, String[], Object[])
	 */
	public List findByHQLNamedParam(final String queryString, final String[] paramNames, final Object[] values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByValueBean(String, Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByValueBean(String, Object)
	 */
	public List findByHQLValueBean(final String queryString, final Object valueBean);

	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit);

	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit,
			final String propertyName, final Object value);

	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit,
			final String[] propertyNames, final Object[] values);

	// -------------------------------------------------------------------------
	// Convenience finder methods for dynamic detached criteria
	// -------------------------------------------------------------------------

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParam(String propertyName, Object value);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param joinEntity the name of the join entity
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParam(String joinEntity, String propertyName, Object value);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @param order {@link org.hibernate.criterion.Order} order instance
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param joinEntity the name of the join entity
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @param order {@link org.hibernate.criterion.Order} order instance
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParam(String[] propertyNames, Object[] values);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param orders {@link org.hibernate.criterion.Order} order instances
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param joinEntitys the names of the join entities
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param orders {@link org.hibernate.criterion.Order} order instances
	 * @return a {@link List} containing the results of the query execution
	 */
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order[] orders);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param joinEntity the name of the join entity
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParam(String joinEntity, String propertyName, Object value, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParam(String propertyName, Object value, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyName the name of the parameter
	 * @param value the value of the parameter, value can be Criterion instance
	 * @param order {@link org.hibernate.criterion.Order} order instance
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParamAndOrder(String propertyName, Object value, Order order, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParam(String[] propertyNames, Object[] values, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param orders {@link org.hibernate.criterion.Order} order instances
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param joinEntitys the names of the join entities
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param orders {@link org.hibernate.criterion.Order} order instances
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values,
			final Order[] orders, final int offset, final int limit);

	public Pagination<T> findPage(PaginationRequest<T> paginationRequest);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public List<T> findByNamedParam(String[] propertyNames, Object[] values, final int offset, final int limit);

	/**
	 * Execute a query based on a dynamically created Hibernate criteria object. use parameters to build DetachedCriteria
	 *
	 * @param propertyNames the names of the parameters
	 * @param values the values of the parameters, values can be Criterion instance
	 * @param orders {@link org.hibernate.criterion.Order} order instances
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, final int offset, final int limit);


	// -------------------------------------------------------------------------
	// Convenience finder methods for named queries
	// -------------------------------------------------------------------------

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedQuery(String, Object...)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedQuery(String, Object...)
	 */
	public List findByNamedQuery(final String queryName, final Object... values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedParam(String, String, Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedParam(String, String, Object)
	 */
	public List findByNamedQueryAndNamedParam(String queryName, String paramName, Object value);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedParam(String, String[], Object[])
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedParam(String, String[], Object[])
	 */
	public List findByNamedQueryAndNamedParam(final String queryName, final String[] paramNames, final Object[] values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByNamedQueryAndValueBean(String, Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByNamedQueryAndValueBean(String, Object)
	 */
	public List findByNamedQueryAndValueBean(final String queryName, final Object valueBean);

	// -------------------------------------------------------------------------
	// Convenience finder methods for detached criteria
	// -------------------------------------------------------------------------

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByCriteria(DetachedCriteria)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByCriteria(DetachedCriteria)
	 */
	public List findByCriteria(DetachedCriteria criteria);

	/**
	 * Execute a query based on a given Hibernate criteria object.
	 * @param criteria the detached Hibernate criteria object.
	 * <b>Note: Do not reuse criteria objects! They need to recreated per execution,
	 * due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @return a {@link Long} the total number of query results
	 */
	public Long findCountByCriteria(final DetachedCriteria criteria);

	/**
	 * Execute a page query based on a given Hibernate criteria object.
	 * @param criteria the detached Hibernate criteria object.
	 * <b>Note: Do not reuse criteria objects! They need to recreated per execution,
	 * due to the suboptimal design of Hibernate's criteria facility.</b>
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByCriteria(final DetachedCriteria criteria, final int offset, final int limit);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#findByExample(Object)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#findByExample(Object)
	 */
	public List<T> findByExample(T exampleEntity);

	/**
	 * Execute a page query based on a given Hibernate criteria object.
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageByExample(final int offset, final int limit);

	/**
	 * Execute a page query based on a given Hibernate criteria object.
	 * @param orders
	 * @param offset the first result to retrieve, numbered from <tt>0</tt>
	 * @param limit the maximum number of results
	 * @return a {@link Pagination} contain query records and the total number of records
	 */
	public Pagination<T> findPageAndOrderByExample(final Order[] orders, final int offset, final int limit);

	/**
	 *
	 * @param propertyNames
	 * @param values
	 * @return
	 */
	public Long findCountByNamedParam(String[] propertyNames, Object[] values);

	/**
	 *
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public Long findCountByNamedParam(String propertyName, Object value);

	// -------------------------------------------------------------------------
	// Convenience query methods for iteration and bulk updates/deletes
	// -------------------------------------------------------------------------

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#iterate(String, Object...)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#iterate(String, Object...)
	 */
	public Iterator iterate(final String queryString, final Object... values);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#closeIterator(Iterator)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#closeIterator(Iterator)
	 */
	public void closeIterator(Iterator it);

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#bulkUpdate(String, Object...)
	 * @see org.springframework.orm.hibernate5.HibernateOperations#bulkUpdate(String, Object...)
	 */
	public int bulkUpdate(final String queryString, final Object... values);

	/**
	 *
	 * @param joinEntitys
	 * @param propertyNames
	 * @param values
	 * @return
	 */
	public DetachedCriteria createDetachedCriteria(String[] joinEntitys, String[] propertyNames, Object[] values);

	/**
	 *
	 * @param joinEntitys
	 * @param propertyNames
	 * @param values
	 * @return
	 */
	public DetachedCriteria createDetachedCriteria(List<String> joinEntitys, List<String> propertyNames, List<Object> values);
}
