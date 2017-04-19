package cn.tongdun.bee.core.hibernate5;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.persistence.TypedQuery;

import cn.tongdun.bee.core.support.PaginationRequest;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import cn.tongdun.bee.core.support.Pagination;

/**
 * @author libinsong1204@gmail.com
 * @date 2012-7-26 上午9:21:53
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernateBaseDaoImpl<T, ID extends Serializable> implements HibernateBaseDao<T, ID> {
	private static final Logger logger = LoggerFactory.getLogger(HibernateBaseDaoImpl.class);
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@Autowired
	private SessionFactory sessionFactory;

	protected Class<T> entityClass;

	protected String entityName;
	
	private boolean checkWriteOperations = true;
	
	private boolean cacheQueries = false;
	
	private String queryCacheRegion;
	
	private int fetchSize = 0;

	private int maxResults = 0;

	@PostConstruct
	public void postConstruct() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			entityClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
		}

		ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityClass);
		entityName = classMetadata.getEntityName();
	}
	
	public LobCreator getLobCreator() {
		return Hibernate.getLobCreator(sessionFactory.getCurrentSession());
	}

	// -------------------------------------------------------------------------
	// Convenience methods for loading individual objects
	// -------------------------------------------------------------------------

	@Override
	public T get(final ID id) {
		return doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				return (T) session.get(entityClass, id);
			}
		});
	}

	@Override
	public T get(final ID id, final LockOptions lockOption) {
		return doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				return (T) session.get(entityClass, id, lockOption);
			}
		});
	}

	@Override
	public T load(final ID id) {
		return doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				return (T) session.load(entityClass, id);
			}
		});
	}

	@Override
	public T load(final ID id, final LockOptions lockOption) {
		return doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				return (T) session.load(entityClass, id, lockOption);
			}
		});
	}

	@Override
	public List<T> loadAll() {
		return doExecute(new HibernateCallback<List<T>>() {

			@Override
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(entityClass);
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				prepareCriteria(criteria);
				return criteria.list();
			}
		});
	}

	@Override
	public void load(final T entity, final ID id) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				session.load(entity, id);
				
				return null;
			}
		});
	}

	@Override
	public void refresh(final T entity) {
		refresh(entity, null);
	}

	@Override
	public void refresh(final T entity, final LockOptions lockOption) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				if(lockOption == null)
					session.refresh(entity);
				else
					session.refresh(entity, lockOption);
				
				return null;
			}
		});
	}

	@Override
	public boolean contains(final T entity) {
		return doExecute(new HibernateCallback<Boolean>() {

			@Override
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				return session.contains(entity);
			}
		});
	}

	@Override
	public void evict(final T entity) {
		doExecute(new HibernateCallback<Boolean>() {

			@Override
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				session.evict(entity);
				return null;
			}
		});
	}

	@Override
	public void initialize(T proxy) {
		Hibernate.initialize(proxy);
	}

	@Override
	public Serializable getIdentifierObject(T entity) {
		if (entity==null) {
			logger.warn("Unable to determine the identifier for an empty object");
			return null;
		}
		ClassMetadata cm = this.sessionFactory.getClassMetadata(entityClass);
		if ( cm==null ) 
			throw new RuntimeException("gIO(): Unable to get class metadata for " + entityClass.getSimpleName());
		return cm.getIdentifier(entity);	
	}

	// -------------------------------------------------------------------------
	// Convenience methods for storing individual objects
	// -------------------------------------------------------------------------

	@Override
	public void lock(final T entity, final LockOptions lockOption) {
		doExecute(new HibernateCallback<Boolean>() {

			@Override
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				session.buildLockRequest(lockOption).lock(entity);
				return null;
			}
		});
	}

	@Override
	public ID save(final T entity) {
		return doExecute(new HibernateCallback<ID>() {

			@Override
			public ID doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				return (ID) session.save(entity);
			}
		});
	}

	@Override
	public void update(T entity) {
		this.update(entity, null);
	}

	@Override
	public void update(final T entity, final LockOptions lockOption) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				session.update(entity);
				if (lockOption != null) {
					session.buildLockRequest(lockOption).lock(entity);
				}
				return null;
			}
		});
	}

	@Override
	public void saveOrUpdate(final T entity) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				session.saveOrUpdate(entity);
				return null;
			}
		});
	}

	@Override
	public void replicate(final T entity, final ReplicationMode replicationMode) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				session.replicate(entity, replicationMode);
				return null;
			}
		});
	}

	@Override
	public void persist(final T entity) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				session.persist(entity);
				return null;
			}
		});
	}

	public T merge(final T entity) {
		return doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				return (T) session.merge(entity);
			}
		});
	}

	@Override
	public void delete(T entity) {
		this.delete(entity, null);
	}

	@Override
	public T delete(ID id) {
		T entity = this.get(id);
		
		if (entity != null) {
			this.delete(entity);
		}
		return entity;
	}

	@Override
	public void delete(final T entity, final LockOptions lockOption) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				if (lockOption != null) {
					session.buildLockRequest(lockOption).lock(entity);
				}
				session.delete(entity);
				return null;
			}
		});
	}

	@Override
	public void deleteAll(final Collection<T> entities) {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				checkWriteOperationAllowed(session);
				for (Object entity : entities) {
					session.delete(entity);
				}
				return null;
			}
		});
	}

	@Override
	public Integer deleteOrUpdateByHQL(final String hql, final String paramName, final Object value) {
		return this.deleteOrUpdateByHQL(hql, new String[]{paramName}, new Object[]{value});
	}

	@Override
	public Integer deleteOrUpdateByHQL(final String hql, final String[] paramNames, final Object[] values) {
		return doExecute(new HibernateCallback<Integer>() {

			@Override
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				TypedQuery query = session.createQuery(hql);
				for(int i=0, len=paramNames.length; i<len; i++) {
					if(values[i] != null) {
						query.setParameter(paramNames[i], values[i]);
					}
				}
				return query.executeUpdate();
			}
		});
	}

	@Override
	public void flush() {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				session.flush();
				return null;
			}
		});
	}

	@Override
	public void clear() {
		doExecute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				session.clear();
				return null;
			}
		});
	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for HQL strings
	// -------------------------------------------------------------------------

	@Override
	public List findByHQL(final String queryString, final Object... values) {
		return doExecute(new HibernateCallback<List>() {

			@Override
			public List doInHibernate(Session session) throws HibernateException, SQLException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}

	@Override
	public List findByHQLNamedParam(String queryString, String paramName, Object value) {
		return findByHQLNamedParam(queryString, new String[] {paramName}, new Object[] {value});
	}

	@Override
	public List findByHQLNamedParam(final String queryString, final String[] paramNames, final Object[] values) {
		if (paramNames.length != values.length) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		
		return doExecute(new HibernateCallback<List>() {

			@Override
			public List doInHibernate(Session session) throws HibernateException, SQLException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}

	@Override
	public List findByHQLValueBean(final String queryString, final Object valueBean) {
		return doExecute(new HibernateCallback<List>() {

			@Override
			public List doInHibernate(Session session) throws HibernateException, SQLException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				queryObject.setProperties(valueBean);
				return queryObject.list();
			}
		});
	}

	@Override
	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit) {
		return this.findPageByHQL(rowSql, countSql, offset, limit, new String[]{}, new Object[]{});
	}

	@Override
	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit, 
			final String propertyName, final Object value) {
		return this.findPageByHQL(rowSql, countSql, offset, limit, new String[]{propertyName}, new Object[]{value});
	}

	@Override
	public Pagination<Object> findPageByHQL(final String rowSql, final String countSql, final int offset, final int limit, 
			final String[] propertyNames, final Object[] values) {
		
		return doExecute(new HibernateCallback<Pagination<Object>>() {
			public Pagination<Object> doInHibernate(Session session) throws HibernateException {
				Query rowQuery = session.createQuery(rowSql).setFirstResult(offset).setMaxResults(limit);
				Query countQuery = session.createQuery(countSql);
				
				for(int i=0, len=propertyNames.length; i<len; i++) {
					if(values[i] != null) {
						rowQuery.setParameter(propertyNames[i], values[i]);
						countQuery.setParameter(propertyNames[i], values[i]);
					}
				}
				long totalRecords = ((Long) countQuery.uniqueResult()).longValue();
				List items = rowQuery.list();

				double totalPages = Math.ceil(totalRecords * 1d / limit);
				Pagination<Object> page = new Pagination<Object>((long)totalPages, offset, limit, totalRecords, items);
				return page;
			}
		});
	}
	
	// -------------------------------------------------------------------------
	// Convenience finder methods for dynamic detached criteria
	// -------------------------------------------------------------------------

	@Override
	public List<T> findByNamedParam(String propertyName, Object value) {
		return this.findByNamedParamAndOrder(null, new String[]{propertyName}, new Object[]{value}, null);
	}

	@Override
	public List<T> findByNamedParam(String joinEntity, String propertyName, Object value) {
		return this.findByNamedParamAndOrder(new String[]{joinEntity}, new String[]{propertyName}, new Object[]{value}, null);
	}

	@Override
	public List<T> findByNamedParamAndOrder(String propertyName, Object value, Order order) {
		return this.findByNamedParamAndOrder(null, new String[]{propertyName}, new Object[]{value}, new Order[]{order});
	}

	@Override
	public List<T> findByNamedParamAndOrder(String joinEntity, String propertyName, Object value, Order order) {
		return this.findByNamedParamAndOrder(new String[]{joinEntity}, new String[]{propertyName}, new Object[]{value}, new Order[]{order});
	}

	@Override
	public List<T> findByNamedParam(String[] propertyNames, Object[] values) {
		return this.findByNamedParamAndOrder(null, propertyNames, values, null);
	}

	@Override
	public List<T> findByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders) {
		return this.findByNamedParamAndOrder(null, propertyNames, values, orders);
	}

	@Override
	public List<T> findByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, Order[] orders) {
		DetachedCriteria criteria = createDetachedCriteria(joinEntitys, propertyNames, values);

		if(orders != null) {
			for (Order order : orders) {
				criteria.addOrder(order);
			}
		}

		return this.findByCriteria(criteria);
	}

	@Override
	public Pagination<T> findPageByNamedParam(String joinEntity, String propertyName, Object value, final int offset, final int limit) {
		return this.findPageByNamedParamAndOrder(new String[]{joinEntity}, new String[]{propertyName}, new Object[]{value}, null, offset, limit);
	}

	@Override
	public Pagination<T> findPageByNamedParam(String propertyName, Object value, final int offset, final int limit) {
		return this.findPageByNamedParamAndOrder(null, new String[]{propertyName}, new Object[]{value}, null, offset, limit);
	}

	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String propertyName, Object value, Order order, final int offset, final int limit) {
		return this.findPageByNamedParamAndOrder(null, new String[]{propertyName}, new Object[]{value}, new Order[]{order}, offset, limit);
	}

	@Override
	public Pagination<T> findPageByNamedParam(String[] propertyNames, Object[] values, final int offset, final int limit) {
		return this.findPageByNamedParamAndOrder(null, propertyNames, values, null, offset, limit);
	}

	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String[] propertyNames, Object[] values, Order[] orders, final int offset, final int limit) {
		return this.findPageByNamedParamAndOrder(null, propertyNames, values, orders, offset, limit);
	}

	@Override
	public Pagination<T> findPageByNamedParamAndOrder(String[] joinEntitys, String[] propertyNames, Object[] values, 
			final Order[] orders, final int offset, final int limit) {
		final DetachedCriteria criteria = createDetachedCriteria(joinEntitys, propertyNames, values);
		
		return doExecute(new HibernateCallback<Pagination<T>>() {
			public Pagination<T> doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);
				
				long totalRecords = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				
				executableCriteria.setProjection(null);
				if(orders != null) {
					for (Order order : orders) {
						criteria.addOrder(order);
					}
				}
				List items = executableCriteria.setFirstResult(offset).setMaxResults(limit).list();

				double totalPages = Math.ceil(totalRecords * 1d / limit);
				Pagination<T> page = new Pagination<T>((long)totalPages, offset, limit, totalRecords, items);
				return page;
			}
		});
	}

	@Override
	public Pagination<T> findPage(final PaginationRequest<T> paginationRequest) {
		final DetachedCriteria criteria = createDetachedCriteria(paginationRequest.getJoinEntitys(), paginationRequest.getPropertyNames(), paginationRequest.getValues());
		return doExecute(new HibernateCallback<Pagination<T>>() {
			public Pagination<T> doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);
				
				long totalRecords = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				
				executableCriteria.setProjection(null);
				if(paginationRequest.getOrders() != null) {
					for (Order order : paginationRequest.getOrders()) {
						criteria.addOrder(order);
					}
				}
				
				int offset = paginationRequest.getOffset();
				int limit = paginationRequest.getLimit();
				List items = executableCriteria.setFirstResult(offset).setMaxResults(limit).list();

				double totalPages = Math.ceil(totalRecords * 1d / limit);
				Pagination<T> page = new Pagination<T>(paginationRequest.getPage(), (long)totalPages, offset, limit, totalRecords, items);
				return page;
			}
		});
		
	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for named queries
	// -------------------------------------------------------------------------

	@Override
	public List findByNamedQuery(final String queryName, final Object... values) {
		return doExecute(new HibernateCallback<List>() {

			@Override
			public List doInHibernate(Session session) throws HibernateException, SQLException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}

	@Override
	public List findByNamedQueryAndNamedParam(String queryName, String paramName, Object value) {
		return findByNamedQueryAndNamedParam(queryName, new String[] {paramName}, new Object[] {value});
	}

	@Override
	public List findByNamedQueryAndNamedParam(final String queryName, final String[] paramNames, final Object[] values) {
		if (paramNames != null && values != null && paramNames.length != values.length) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return doExecute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}

	@Override
	public List findByNamedQueryAndValueBean(final String queryName, final Object valueBean) {
		return doExecute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				queryObject.setProperties(valueBean);
				return queryObject.list();
			}
		});
	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for detached criteria
	// -------------------------------------------------------------------------

	@Override
	public List findByCriteria(final DetachedCriteria criteria) {
		return doExecute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);
				return executableCriteria.list();
			}
		});
	}

	@Override
	public Long findCountByCriteria(final DetachedCriteria criteria) {
		return doExecute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);
				
				long totalCount = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				return totalCount;
			}
		});
	}

	@Override
	public Pagination<T> findPageByCriteria(final DetachedCriteria criteria, final int offset, final int limit) {
		return doExecute(new HibernateCallback<Pagination<T>>() {
			public Pagination<T> doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);
				
				long totalRecords = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				executableCriteria.setProjection(null);
				List items = executableCriteria.setFirstResult(offset).setMaxResults(limit).list();

				double totalPages = Math.ceil(totalRecords * 1d / limit);
				Pagination<T> page = new Pagination<T>((long)totalPages, offset, limit, totalRecords, items);
				return page;
			}
		});
	}

	@Override
	public List<T> findByExample(final T exampleEntity) {
		return doExecute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = (entityName != null ?
						session.createCriteria(entityName) : session.createCriteria(exampleEntity.getClass()));
				executableCriteria.add(Example.create(exampleEntity));
				prepareCriteria(executableCriteria);
				return executableCriteria.list();
			}
		});
	}

	@Override
	public Pagination<T> findPageByExample(final int offset, final int limit) {
		return this.findPageAndOrderByExample(null, offset, limit);
	}

	@Override
	public Pagination<T> findPageAndOrderByExample(final Order[] orders, final int offset, final int limit) {
		return doExecute(new HibernateCallback<Pagination<T>>() {
			public Pagination<T> doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = session.createCriteria(entityClass);
				prepareCriteria(executableCriteria);

				if(orders != null) {
					for(Order order : orders) {
						executableCriteria.addOrder(order);
					}
				}

				long totalRecords = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				executableCriteria.setProjection(null);
				List items = executableCriteria.setFirstResult(offset).setMaxResults(limit).list();

				double totalPages = Math.ceil(totalRecords * 1d / limit);
				Pagination<T> page = new Pagination<T>((int)totalPages, offset, limit, totalRecords, items);
				return page;
			}
		});
	}

	@Override
	public Long findCountByNamedParam(String propertyName, Object value) {
		return this.findCountByNamedParam(new String[]{propertyName}, new Object[]{value});
	}

	@Override
	public Long findCountByNamedParam(String[] propertyNames, Object[] values) {
		final DetachedCriteria criteria = createDetachedCriteria(null, propertyNames, values);
		return doExecute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = criteria.getExecutableCriteria(session);
				prepareCriteria(executableCriteria);

				long totalCount = ((Long) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
				return totalCount;
			}
		});
	}

	// -------------------------------------------------------------------------
	// Convenience query methods for iteration and bulk updates/deletes
	// -------------------------------------------------------------------------

	@Override
	public Iterator iterate(final String queryString, final Object... values) {
		return doExecute(new HibernateCallback<Iterator>() {
			public Iterator doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.iterate();
			}
		});
	}

	@Override
	public void closeIterator(Iterator it) {
		Hibernate.close(it);
	}

	@Override
	public int bulkUpdate(final String queryString, final Object... values) {
		return doExecute(new HibernateCallback<Integer>() {
			public Integer doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.executeUpdate();
			}
		});
	}

	// -------------------------------------------------------------------------
	// Helper methods used by the operations above
	// -------------------------------------------------------------------------
	protected void prepareCriteria(Criteria criteria) {
		if (this.isCacheQueries()) {
			criteria.setCacheable(true);
			if (this.getQueryCacheRegion() != null) {
				criteria.setCacheRegion(this.getQueryCacheRegion());
			}
		}
		if (this.getFetchSize() > 0) {
			criteria.setFetchSize(this.getFetchSize());
		}
		if (this.getMaxResults() > 0) {
			criteria.setMaxResults(this.getMaxResults());
		}
	}

	@Override
	public DetachedCriteria createDetachedCriteria(String[] joinEntitys, String[] propertyNames, Object[] values) {
		if(joinEntitys != null)
			return createDetachedCriteria(Arrays.asList(joinEntitys), Arrays.asList(propertyNames), Arrays.asList(values));
		else
			return createDetachedCriteria(null, Arrays.asList(propertyNames), Arrays.asList(values));
	}

	@Override
	public DetachedCriteria createDetachedCriteria(List<String> joinEntitys, List<String> propertyNames, List<Object> values) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);

		if(joinEntitys != null) {
			for (String joinEntity : joinEntitys) {
				criteria.setFetchMode(joinEntity, FetchMode.JOIN);
				criteria.createAlias(joinEntity, joinEntity);
			}
		}

		for (int i = 0, len = propertyNames.size(); i < len; i++) {
			String propertyName = propertyNames.get(i);
			Object value = values.get(i);
			
			if (value instanceof Criterion) {
				criteria.add((Criterion) value);
			} else if (value instanceof Collection) {
				criteria.add(Restrictions.in(propertyName, (Collection) value));
			} else if (value.getClass().isArray()) {
				criteria.add(Restrictions.in(propertyName, (Object[])value));
			} else if (value instanceof Map) {
				Iterator<Entry<String, Object>> iterator = ((Map<String, Object>)value).entrySet().iterator();
				
				Criterion lhs, rhs;
				Entry<String, Object> entry = iterator.next();
				if("like".equals(propertyName)) {
					lhs = Restrictions.like(entry.getKey(), entry.getValue());
					entry = iterator.next();
					rhs = Restrictions.like(entry.getKey(), entry.getValue());
				} else {
					lhs = Restrictions.eq(entry.getKey(), entry.getValue());
					entry = iterator.next();
					rhs = Restrictions.eq(entry.getKey(), entry.getValue());
				}
				lhs = Restrictions.or(lhs, rhs);
				
				while(iterator.hasNext()) {
					entry = iterator.next();
					if("like".equals(propertyName)) {
						rhs = Restrictions.like(entry.getKey(), entry.getValue());
					} else {
						rhs = Restrictions.eq(entry.getKey(), entry.getValue());
					}
					lhs = Restrictions.or(lhs, rhs);
				}
				
				criteria.add(lhs);
			} else {
				criteria.add(Restrictions.eq(propertyName, value));
			}
		}
		return criteria;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	protected <R> R doExecute(HibernateCallback<R> action) {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			R result = action.doInHibernate(session);
			return result;
		} catch (HibernateException e) {
			throw convertHibernateAccessException(e);
		} catch (SQLException e) {
			throw convertJdbcAccessException(e);
		}
	}
	
	private SQLExceptionTranslator jdbcExceptionTranslator;

	private SQLExceptionTranslator defaultJdbcExceptionTranslator;
	
	
	public DataAccessException convertHibernateAccessException(HibernateException ex) {
		if (getJdbcExceptionTranslator() != null && ex instanceof JDBCException) {
			return convertJdbcAccessException((JDBCException) ex, getJdbcExceptionTranslator());
		}
		else if (GenericJDBCException.class.equals(ex.getClass())) {
			return convertJdbcAccessException((GenericJDBCException) ex, getDefaultJdbcExceptionTranslator());
		}
		return SessionFactoryUtils.convertHibernateAccessException(ex);
	}
	
	protected DataAccessException convertJdbcAccessException(JDBCException ex, SQLExceptionTranslator translator) {
		return translator.translate("Hibernate operation: " + ex.getMessage(), ex.getSQL(), ex.getSQLException());
	}
	
	protected DataAccessException convertJdbcAccessException(SQLException ex) {
		SQLExceptionTranslator translator = getJdbcExceptionTranslator();
		if (translator == null) {
			translator = getDefaultJdbcExceptionTranslator();
		}
		return translator.translate("Hibernate-related JDBC operation", null, ex);
	}
	
	protected void checkWriteOperationAllowed(Session session) {
		if (isCheckWriteOperations() && session.isDefaultReadOnly()) {
			throw new InvalidDataAccessApiUsageException(
					"Write operations are not allowed in read-only mode (FlushMode.MANUAL): "+
					"Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.");
		}
	}
	
	protected void prepareQuery(Query queryObject) {
		if (isCacheQueries()) {
			queryObject.setCacheable(true);
			if (getQueryCacheRegion() != null) {
				queryObject.setCacheRegion(getQueryCacheRegion());
			}
		}
		if (getFetchSize() > 0) {
			queryObject.setFetchSize(getFetchSize());
		}
		if (getMaxResults() > 0) {
			queryObject.setMaxResults(getMaxResults());
		}
	}
	
	protected void applyNamedParameterToQuery(Query queryObject, String paramName, Object value)
			throws HibernateException {

		if (value instanceof Collection) {
			queryObject.setParameterList(paramName, (Collection) value);
		}
		else if (value instanceof Object[]) {
			queryObject.setParameterList(paramName, (Object[]) value);
		}
		else {
			queryObject.setParameter(paramName, value);
		}
	}
	
	public SQLExceptionTranslator getJdbcExceptionTranslator() {
		return jdbcExceptionTranslator;
	}

	public void setJdbcExceptionTranslator(
			SQLExceptionTranslator jdbcExceptionTranslator) {
		this.jdbcExceptionTranslator = jdbcExceptionTranslator;
	}

	public SQLExceptionTranslator getDefaultJdbcExceptionTranslator() {
		return defaultJdbcExceptionTranslator;
	}

	public void setDefaultJdbcExceptionTranslator(
			SQLExceptionTranslator defaultJdbcExceptionTranslator) {
		this.defaultJdbcExceptionTranslator = defaultJdbcExceptionTranslator;
	}
	
	public void setCheckWriteOperations(boolean checkWriteOperations) {
		this.checkWriteOperations = checkWriteOperations;
	}

	public boolean isCheckWriteOperations() {
		return this.checkWriteOperations;
	}
	
	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	public boolean isCacheQueries() {
		return this.cacheQueries;
	}
	
	public void setQueryCacheRegion(String queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	public String getQueryCacheRegion() {
		return this.queryCacheRegion;
	}
	
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public int getFetchSize() {
		return this.fetchSize;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMaxResults() {
		return this.maxResults;
	}
}