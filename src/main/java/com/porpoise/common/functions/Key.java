package com.porpoise.common.functions;

import com.google.common.base.Equivalence;

public interface Key<T> extends Equivalence<T>{
	
	public int hashCode();
	
	public boolean equals(Object other);

}
