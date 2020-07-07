package com.lgcns.suno.net.p2p.client.sharing;

import java.util.Vector;

public class FileManager {
	public static final int STATUS_SHARED				= 0;
	public static final int STATUS_NOT_COMMIT	= 1;
	public static final int STATUS_NOT_EXISTS		= 2;
	public static final int STATUS_INCOMMING		= 3;

	private static Vector<SharedFile> mFiles = new Vector<SharedFile>();

	public static void addFile(String filename) {
		addFile(new SharedFile(filename));
	}

	public static void addFile(SharedFile f) {
		if (!mFiles.contains(f))
			mFiles.add(f);
	}

	public static SharedFile getFile(String filename) {
		for (int i = 0; i < mFiles.size(); i++)
			if (mFiles.get(i).getFileName().equalsIgnoreCase(filename))
				return mFiles.get(i);

		return null;
	}

	public static SharedFile getFile(int fileid) {
		for (int i = 0; i < mFiles.size(); i++)
			if (mFiles.get(i).getFileId() == fileid)
				return mFiles.get(i);

		return null;
	}

	/*
	public static void commit() {
		for (SharedFile f: mFiles) {
			if (f.getFileStatus() == STATUS_NOT_COMMIT) {
				Packet p = new Packet();
				p.setType(PacketHandler.TYPE_REGISTER_FILE);
				p.setOption((byte) 0);
				p.setPayload(f.getFileName() + "\t" + f.getFileSize() + "\t" + f.getFileMD5());

				PeerManager.get(0).write(p);
			}
		}
	}
	*/
}
