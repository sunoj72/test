package com.lgcns.test.suno.model;

public class SimpleMessageExecutor implements IMessageExecutor {

	@Override
	public void execute(String msg) {
		System.out.println(msg);
	}

}
