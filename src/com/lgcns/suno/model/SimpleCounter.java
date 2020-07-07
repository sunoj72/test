package com.lgcns.test.suno.model;

import java.util.HashMap;
import java.util.Map;

public class SimpleCounter implements ICounter {
	private HashMap<String, Integer> counters = new HashMap<>();
	
	@Override
	public Map<String, Integer> getCounters() {
		return this.counters;
	}

	@Override
	public void append(IEntity entity) {
		if (this.counters.containsKey(entity.getKey())) {
			this.counters.put(entity.getKey(), this.counters.get(entity.getKey()) + 1);
		} else {
			this.counters.put(entity.getKey(), 1);
		}
	}

	public void printCounter() {
		for(Map.Entry<String, Integer> kv : this.counters.entrySet()) {
			System.out.println(String.format("%s:%d", kv.getKey(), kv.getValue()));
		}
	}
}
