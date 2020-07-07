package com.lgcns.suno.net.p2p.server.sharing;

public class SharedFile {
	private int mFileId;
	private String mFileName;
	private long mFileSize;
	private String mFileMD5;

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
	public void setFileSize(long mFileSize) {
		this.mFileSize = mFileSize;
	}
	public String getFileMD5() {
		return mFileMD5;
	}
	public void setFileMD5(String mFileMD5) {
		this.mFileMD5 = mFileMD5;
	}

	public SharedFile(String filename, long filelen, String filemd5) {
		this.mFileName = filename;
		this.mFileSize = filelen;
		this.mFileMD5 = filemd5;
	}

}
