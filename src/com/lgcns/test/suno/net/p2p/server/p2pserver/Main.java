package com.lgcns.suno.net.p2p.server.p2pserver;

import java.lang.Thread.State;

import com.lgcns.suno.net.p2p.server.peers.PeerManager;

public class Main {
	private static Listener mListener = null;
	private static Console mConsole = null;

	public static void endWork() {
		mConsole.setRunning(false);
		mListener.setRunning(false);
		for (int i = 0; i < PeerManager.size(); i++)
			PeerManager.get(i).setRunning(false);
		mListener.close();
	}

	public static void main(String[] args) {
		System.out.println("P2P Server v1.0\n");

		int port;
		try { port = Integer.parseInt(args[1]); } catch (Exception e) { port = 9090; }

		Console.openLogger();

		mListener = new Listener(port);
		mConsole = new Console();

		while  (mListener.getState() != State.RUNNABLE);
		mConsole.setCanRead(true);

	}

}
