package com.lgcns.suno.net.p2p.server.sharing;

import java.util.Hashtable;
import java.util.Vector;

import com.lgcns.suno.net.p2p.server.peers.Peer;
import com.lgcns.suno.net.p2p.server.peers.PeerManager;

public class FileManager {
	private static int mLastFileId = 0;
	private static Hashtable<String, Integer> mFileIds = new Hashtable<String, Integer>();										//	md5, id
	private static Hashtable<Peer, Vector<SharedFile>> mPeerFiles = new Hashtable<Peer, Vector<SharedFile>>();	// peer, files

	public static int getLastFileId() {
		return mLastFileId - 1;
	}

	public static int getFileId(String md5) {
		if (!mFileIds.containsKey(md5)) mFileIds.put(md5, mLastFileId++);

		return mFileIds.get(md5);
	}

	public static void addFile(Peer p, String filename, long filelen, String filemd5) {
		addFile(p, new SharedFile(filename, filelen, filemd5));
	}

	public static void addFile(Peer p, SharedFile f) {
		if (!mPeerFiles.containsKey(p))
			mPeerFiles.put(p, new Vector<SharedFile>());
		f.setFileId(getFileId(f.getFileMD5()));
		mPeerFiles.get(p).add(f);
	}

	public static Vector<SharedFile> getPeerFiles(Peer p) {
		return mPeerFiles.get(p);
	}

	public static void findPeerFile(Peer p, String filename, Vector<SharedFile> files) {
		files.clear();

		if (mPeerFiles.containsKey(p))
			for (SharedFile f: mPeerFiles.get(p))
				if (f.getFileName().contains(filename))
						files.add(f);
	}

	public static void findPeerFile(Peer p, int fileid, Vector<SharedFile> files) {
		files.clear();

		if (mPeerFiles.containsKey(p))
			for (SharedFile f: mPeerFiles.get(p))
				if (f.getFileId() == fileid)
						files.add(f);
	}

	public static void findFile(String filename, Hashtable<Peer, Vector<SharedFile>> files) {
		files.clear();

		for (int i = 0; i < PeerManager.size(); i++) {
			Peer p = PeerManager.get(i);
			Vector<SharedFile> tmp = new Vector<SharedFile>();
			findPeerFile(p, filename, tmp);
			if (tmp.size() > 0) files.put(p, tmp);
		}
	}

	public static void findFile(int fileid, Hashtable<Peer, Vector<SharedFile>> files) {
		files.clear();

		for (int i = 0; i < PeerManager.size(); i++) {
			Peer p = PeerManager.get(i);
			Vector<SharedFile> tmp = new Vector<SharedFile>();
			findPeerFile(p, fileid, tmp);
			if (tmp.size() > 0) files.put(p, tmp);
		}
	}
}
