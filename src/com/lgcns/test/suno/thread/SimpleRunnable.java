package com.lgcns.test.suno.thread;

import com.lgcns.test.suno.model.SimpleEntity;

public class SimpleRunnable implements Runnable {
	private SimpleEntity entity = null;

	public SimpleRunnable(SimpleEntity entity) {
		this.entity = entity;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
