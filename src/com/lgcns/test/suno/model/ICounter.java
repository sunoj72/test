package com.lgcns.test.suno.model;

import java.util.Map;

public interface ICounter {
	public Map<String, Integer> getCounters();
	public void append(IEntity entity);
//	public void printCounter();
}
