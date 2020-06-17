package com.lgcns.suno.net.p2p.server.peers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;

import com.lgcns.suno.net.p2p.server.p2pserver.Console;
import com.lgcns.suno.net.p2p.server.packets.Packet;
import com.lgcns.suno.net.p2p.server.packets.PacketHandler;

public class Peer extends Thread {
	private int mPeerIndex;
	private Socket mSocket;
	private String mPeerAddress;
	private int mPeerPort;
	private String mPeerId;
	private boolean mLeaderPeer = false;
	private boolean mHolePunched = false;
	private Socket mHolePunchedSocket;
	private long mLastHeartbeat = 0;
	private int mLoads;
	private boolean mInitialized = false;
	private boolean mRunning = false;

	public boolean isRunning() {
		return mRunning;
	}

	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public int getPeerIndex() {
		return mPeerIndex;
	}

	public String getRemoteAddress() {
		return mPeerAddress;
	}

	public int getRemotePort() {
		return mPeerPort;
	}

	public String getPeerId() {
		if (mPeerId == null) return mPeerAddress;
		return mPeerId;
	}

	public void setPeerId(String mPeerId) {
		this.mPeerId = mPeerId;
		this.mInitialized = true;
	}

	public int getLoads() {
		return mLoads;
	}

	public void setLoads(int mLoads) {
		this.mLoads = mLoads;
	}

	public boolean isLeaderPeer() {
		return mLeaderPeer;
	}

	public void setLeaderPeer(boolean mLeaderPeer) {
		this.mLeaderPeer = mLeaderPeer;
	}

	public boolean isHolePunched() {
		return mHolePunched;
	}

	public Socket getHolePunchedSocket() {
		return mHolePunchedSocket;
	}

	public void setHolePunchedSocket(Socket mHolePunchedSocket) {
		this.mHolePunchedSocket = mHolePunchedSocket;
	}

	public void setLastHeartbeat(long mLastHeartbeat) {
		this.mLastHeartbeat = mLastHeartbeat;
	}

	public boolean isInitialized() {
		return mInitialized;
	}

	public OutputStream getOutputStream() {
		try {
			return mSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void write(Packet p) {
		char buf[] = new char[p.getPayloadSize() + 6];

		buf[0] = (char) p.getType();
		buf[1] = (char) p.getOption();
		int szPayload = p.getPayloadSize();
		buf[2] = (char) ((szPayload & 0x000000FF));
		buf[3] = (char) ((szPayload & 0x0000FF00) >> 8);
		buf[4] = (char) ((szPayload & 0x00FF0000) >> 16);
		buf[5] = (char) ((szPayload & 0xFF000000) >> 24);
		if (szPayload > 0) {
			char pl[] = p.getPayload().toCharArray();
			System.arraycopy(pl, 0, buf, 6, szPayload);
		}

		write(buf);
	}

	public void write(char[] buf) {
		try {
			//System.out.println("write: " + "(t: " + (int)buf[0] + "), (o: " + (int)buf[1] + ") " + new String(buf, 6, buf.length - 6));
			BufferedWriter bw;
			bw = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			bw.write(buf);
			bw.flush();
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.mRunning = false;
		}
	}

	public Peer(int idx, Socket c) {
		this.mPeerIndex = idx;
		this.mSocket = c;
		this.mPeerAddress = c.getRemoteSocketAddress().toString().substring(1);
		this.mPeerPort = Integer.parseInt(this.mPeerAddress.substring(this.mPeerAddress.indexOf(':') + 1));
		this.mPeerAddress = this.mPeerAddress.substring(0, this.mPeerAddress.indexOf(':'));

		this.mRunning = true;
		this.start();
	}

	@Override
	public void run() {
		try {
			InputStream is = mSocket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			mLastHeartbeat = Calendar.getInstance().getTimeInMillis();
			while (mRunning) {
				if (is.available() > 0) {
					char buf[] = new char[Math.min(Packet.MAX_PAYLOAD_SIZE, is.available())];
					br.read(buf);
					Packet p = new Packet(buf);
					PacketHandler.doPacket(this, p);
				}

				if (Calendar.getInstance().getTimeInMillis() > mLastHeartbeat + 5000) {
					Packet p = new Packet();
					p.setType(PacketHandler.TYPE_HEART_BEAT);
					write(p);
					mLastHeartbeat = Calendar.getInstance().getTimeInMillis();
				}

				try { Thread.sleep(10); } catch (InterruptedException e) { }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mRunning = false;
			//e.printStackTrace();
		}

		try { this.mSocket.close(); } catch (IOException e) { }
		PeerManager.remove(this);
		Console.write(this, "�뿰寃곗씠 醫낅즺�릺�뿀�뒿�땲�떎.");
	}

}
