package com.lgcns.suno.net.p2p.server.packets;

public class Packet {
	public static final int MAX_PAYLOAD_SIZE = 512 * 8; //4KB

	private byte mType;
	private byte mOption;
	private int mPayloadSize;
	private char[] mPayload;

	public byte getType() {
		return mType;
	}

	public void setType(byte mType) {
		this.mType = mType;
	}

	public byte getOption() {
		return mOption;
	}

	public void setOption(byte mOption) {
		this.mOption = mOption;
	}

	public int getPayloadSize() {
		return mPayloadSize;
	}

	public String getPayload() {
		return new String(mPayload);
	}

	public void setPayload(char[] mPayload) {
		this.mPayload = mPayload;
		this.mPayloadSize = this.mPayload.length;
	}

	public void setPayload(String mPayload) {
		this.mPayload = mPayload.toCharArray();
		this.mPayloadSize = this.mPayload.length;
	}

	public Packet() {
		this.mType = -1;
		this.mOption = 0;
		this.mPayloadSize = 0;
		this.mPayload = null;
	}

	public Packet(char[] msg) {
		this.mType = (byte) msg[0];
		this.mOption = (byte) msg[1];
		this.mPayloadSize =  (int) (msg[2] + msg[3] * 8 + msg[4] * 16 + msg[5] * 24);
		this.mPayload = new char[mPayloadSize];
		if (mPayloadSize > 0)
			System.arraycopy(msg, 6, this.mPayload, 0, mPayloadSize);
	}

}
