package com.lgcns.suno.net.p2p.client.peers;

import java.util.Vector;

public class PeerManager {
	private static int mLastIndex = 0;
	private static Vector<Peer> mPeers = new Vector<Peer>();

	public PeerManager() {
		//mStreams = new Vector<InputStream>();
	}

	public static void add(Peer p) {
		mPeers.add(p);
		//mStreams.add(is);
	}

	public static void remove(Peer p) {
		mPeers.remove(p);
		//mStreams.remove(is);
	}

	public static int size() {
		return mPeers.size();
	}

	public static Peer get(int index) {
		return mPeers.get(index);
		//return mStreams.get(index);
	}

	public static int popIndex() {
		return mLastIndex++;
	}
}
