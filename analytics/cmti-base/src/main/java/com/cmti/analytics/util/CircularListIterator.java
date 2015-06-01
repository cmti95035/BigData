package com.cmti.analytics.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Lists;

/**
 * 
 //https://www.google.com/webhp?sourceid=chrome-instant&rlz=1C1CHMO_enUS558US558&ion=1&espv=2&ie=UTF-8#q=circular+list+java
 * 
 * see com.google.common.collect.Iterators source code Doubly-Linked Circular
 * Linked List. add() remove() set() methods are not implemented.
 * 
 * @author Guobiao Mo
 *
 */
public class CircularListIterator<T> implements ListIterator<T> {
	List<T> list;
	ListIterator<T> iterator;

	public CircularListIterator(T[] array) {
		this(Arrays.asList(array));
	}

	public CircularListIterator(List<T> list) {
		iterator = Collections.emptyListIterator();
		this.list = list;
	}

	@Override
	public boolean hasNext() {
		if (!iterator.hasNext()) {
			iterator = list.listIterator();
		}
		return iterator.hasNext();
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return iterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPrevious() {
		if (!iterator.hasPrevious()) {
			iterator = list.listIterator(list.size());
		}
		return iterator.hasPrevious();
	}

	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return iterator.previous();
	}

	@Override
	public int nextIndex() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return iterator.nextIndex();
	}

	@Override
	public int previousIndex() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return iterator.previousIndex();
	}

	@Override
	public void set(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(T e) {
		throw new UnsupportedOperationException();
	}

	public static void main(final String[] args) {
		List<String> list = Lists.newArrayList("A", "B", "C");
		final CircularListIterator<String> circular = new CircularListIterator<String>(list);
		
		for (int i = 0; i < 8; i++) {
			System.out.println(i + circular.next());
		}
		
		System.out.println("----");
		
		for (int i = 0; i < 11; i++) {
			System.out.println(i + circular.previous());
		}
	}
}