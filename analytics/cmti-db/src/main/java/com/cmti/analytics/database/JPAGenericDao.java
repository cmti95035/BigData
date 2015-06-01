package com.cmti.analytics.database;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

/** A wrapper on GenericDAOImpl that allows Spring to inject EntityManager and JPASearchProcessor
 * 
 * @author Guobiao Mo
 *
 * @param <T> domain class
 * @param <ID> db key
 */

@Repository
public class JPAGenericDao<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {
	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}
	
	@Override
	@Autowired
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}
}
