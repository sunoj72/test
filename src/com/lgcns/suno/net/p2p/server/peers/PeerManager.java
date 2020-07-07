package com.lgcns.suno.net.p2p.server.peers;

import java.util.Vector;

public class PeerManager {
	private static int mLastIndex = 0;
	private static Vector<Peer> mPeers = new Vector<Peer>();

	public PeerManager() {
	}

	public static void add(Peer p) {
		mPeers.add(p);
	}

	public static void remove(Peer p) {
		mPeers.remove(p);
	}

	public static int size() {
		return mPeers.size();
	}

	public static Peer get(int index) {
		return mPeers.get(index);
	}

	public static Peer get(String peerid) {
		for (Peer p: mPeers)
			if (p.getPeerId().equals(peerid))
				return p;

		return null;
	}

	public static int popIndex() {
		return mLastIndex++;
	}

	public static int checkName(String name) {
		//理쒖냼 4 ~ 理쒕� 12�옄�쓽 �븣�뙆踰녠낵 �닽�옄濡� �씠猷⑥뼱吏� 臾몄옣, 泥� 臾몄옄�뒗 �븣�뙆踰�
		if (name.matches("^[a-zA-Z][a-zA-Z0-9]{3,11}")) {
			for (Peer p: mPeers)
				if (p.getPeerId().equals(name)) return 1;
			return 0;
		}
		return 2;
	}

	public static Peer getMinLoad(Peer ex) {
		int ld = Integer.MAX_VALUE;
		Peer rst = null;

		for (Peer p: mPeers)
			if ((p.isLeaderPeer() || p.isHolePunched()) && p.getLoads() < ld && p != ex) {
				rst = p;
				ld = rst.getLoads();
			}

		return rst;
	}
}
