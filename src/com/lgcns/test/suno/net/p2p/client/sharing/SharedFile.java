package com.lgcns.suno.net.p2p.client.sharing;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class SharedFile {
	private int mFileId;
	private String mFileName;
	private long mFileSize;
	private String mFileMD5;
	private int mFileStatus;

	public int getFileId() {
		return mFileId;
	}
	public void setFileId(int mFileId) {
		this.mFileId = mFileId;
	}
	public String getFileName() {
		return mFileName;
	}
	public void setFileName(String mFileName) {
		this.mFileName = mFileName;
	}
	public long getFileSize() {
		return mFileSize;
	}
	public void setFileSize(long mFileSize) {
		this.mFileSize = mFileSize;
	}
	public String getFileSize2() {
		char sizech[] = {'B', 'K', 'M', 'G', 'T'};
		float filesize = this.mFileSize;
		int sizeidx = 0;
		while (filesize > 512) {
			filesize /= 512;
			sizeidx++;
		}

		return String.format("%3.2f %c", filesize, sizech[sizeidx]);
	}
	public String getFileMD5() {
		return mFileMD5;
	}
	public void setFileMD5(String mFileMD5) {
		this.mFileMD5 = mFileMD5;
	}
	public int getFileStatus() {
		return mFileStatus;
	}
	public void setFileStatus(int mFileStatus) {
		this.mFileStatus = mFileStatus;
	}

	public static boolean exists(String filename) {
		return new File(filename).exists();
	}

	public static String MakeMD5(String filepath) {
		String ret = "";

		try {
			File f = new File(filepath);
			FileInputStream fs = new FileInputStream(f);
			byte[] stream = new byte[(int) f.length()];
			fs.read(stream);
			fs.close();
			ret = MakeMD5(stream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return ret;
	}

	public static String MakeMD5(byte[] stream) {
		 StringBuffer md5 = new StringBuffer();

		try {
			byte[] digest = MessageDigest.getInstance("MD5").digest(stream);

			for (int i = 0; i < digest.length; i++) {
				md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
				md5.append(Integer.toString(digest[i] & 0x0f, 16));
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return md5.toString();
	}

	public SharedFile(String filename) {
		this.mFileName = filename;
		if (exists(filename)) {
			this.mFileSize = new File(filename).length();
			this.mFileMD5 = MakeMD5(filename);
			this.mFileStatus = FileManager.STATUS_NOT_COMMIT;
		} else {
			this.mFileStatus = FileManager.STATUS_NOT_EXISTS;
		}
	}

}
