package com.lgcns.suno.net.p2p.client.packets;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.lgcns.suno.net.p2p.client.peers.Peer;

public class RelayManager {
	public static final int TYPE_RELAY_TO		= 0;
	public static final int TYPE_RELAY_SELF	= 1;

	private static Hashtable<String, Integer> mRelayType = new Hashtable<String, Integer>();
	private static Hashtable<String, Peer> mRelayTo = new Hashtable<String, Peer>();
	private static Hashtable<String, String> mRelayFileId = new Hashtable<String, String>();

	public static void addRelay(String auth, String fileid) {
		if (mRelayType.containsKey(auth)) return;

		mRelayType.put(auth, TYPE_RELAY_SELF);
		mRelayFileId.put(auth, fileid);
	}

	public static String addRelay(Peer to, String fileid) {
		String auth = makeAuth();

		mRelayType.put(auth, TYPE_RELAY_TO);
		mRelayTo.put(auth, to);
		mRelayFileId.put(auth, fileid);

		return auth;
	}

	public static String makeAuth() {
		String auth = "req" + ((Long) Calendar.getInstance().getTimeInMillis()).toString();

		while (mRelayType.containsKey(auth))
			auth = "req" + ((Long) Calendar.getInstance().getTimeInMillis()).toString();

		return auth;
	}

	public static int getType(String auth) {
		if (!mRelayType.containsKey(auth)) return -1;

		return mRelayType.get(auth);
	}

	public static Peer getPeer(String auth) {
		if (!mRelayTo.containsKey(auth)) return null;

		return mRelayTo.get(auth);
	}

	public static void replaceAuth(String auth1, String auth2) {
		if (!mRelayType.containsKey(auth1)) return;

		int type = mRelayType.get(auth1);
		Peer to = null;
		if (type == TYPE_RELAY_TO) to = mRelayTo.get(auth1);
		String fileid = mRelayFileId.get(auth1);

		mRelayType.remove(auth1);
		if (type == TYPE_RELAY_TO) mRelayTo.remove(auth1);
		mRelayFileId.remove(auth1);

		mRelayType.put(auth2, type);
		if (type == TYPE_RELAY_TO) mRelayTo.put(auth2, to);
		mRelayFileId.put(auth2, fileid);
	}

	public static int removeRelay(String auth) {
		if (!mRelayType.containsKey(auth)) return -1;

		int type = mRelayType.get(auth);
		mRelayType.remove(auth);
		if (type == TYPE_RELAY_TO) mRelayTo.remove(auth);
		mRelayFileId.remove(auth);

		return type;
	}
}
