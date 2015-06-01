package com.googlecode.genericdao.search.jpa.hibernate;

//import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;

import com.googlecode.genericdao.search.MetadataUtil;

public class HibernateMetadataUtil {
	public static MetadataUtil getInstanceForEntityManagerFactory(HibernateEntityManagerFactory emf) {
		return com.googlecode.genericdao.search.hibernate.HibernateMetadataUtil.getInstanceForSessionFactory(emf.getSessionFactory());
	}
}