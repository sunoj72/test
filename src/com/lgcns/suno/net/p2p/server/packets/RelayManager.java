package com.lgcns.suno.net.p2p.server.packets;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.lgcns.suno.net.p2p.server.peers.Peer;

public class RelayManager {
	private static final int RELAY_TIMEOUT = 60 * 1000;

	private static Hashtable<String, Peer> mRelayPeer = new Hashtable<String, Peer>();
	private static Hashtable<String, Long> mRelayTimeOut = new Hashtable<String, Long>();

	public static boolean addRelay(String auth, Peer from) {
		if (mRelayPeer.containsKey(auth)) return false;

		mRelayPeer.put(auth, from);
		mRelayTimeOut.put(auth, Calendar.getInstance().getTimeInMillis() + RELAY_TIMEOUT);
		return true;
	}

	public static String addRelay(Peer from) {
		String auth = makeAuth();
		while (!addRelay(auth, from))
			auth = makeAuth();

		return auth;
	}

	public static Peer getRelay(String auth) {
		if (mRelayPeer.containsKey(auth))
			return mRelayPeer.get(auth);

		return null;
	}

	public static void removeRelay(String auth) {
		if (!mRelayPeer.containsKey(auth)) return;

		mRelayPeer.remove(auth);
		mRelayTimeOut.remove(auth);
	}

	public static String makeAuth() {
		String auth = ((Long) Calendar.getInstance().getTimeInMillis()).toString();

		while (mRelayPeer.containsKey(auth))
			if (mRelayTimeOut.get(auth) > Calendar.getInstance().getTimeInMillis()) {
				mRelayPeer.remove(auth);
				mRelayTimeOut.remove(auth);
			}
			auth = ((Long) Calendar.getInstance().getTimeInMillis()).toString();

		return auth;
	}

}
