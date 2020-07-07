package com.lgcns.suno.net.p2p.client.p2ppeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import com.lgcns.suno.net.p2p.client.packets.Packet;
import com.lgcns.suno.net.p2p.client.packets.PacketHandler;
import com.lgcns.suno.net.p2p.client.peers.Peer;
import com.lgcns.suno.net.p2p.client.peers.PeerManager;
import com.lgcns.suno.net.p2p.client.sharing.FileManager;
import com.lgcns.suno.net.p2p.client.sharing.SharedFile;

public class Console extends Thread {
	private static boolean mJustPrompted = false;

	private boolean mRunning = false;
	private boolean mCanRead = false;

	public boolean isRunning() {
		return mRunning;
	}

	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public void setCanRead(boolean mCanRead) {
		this.mCanRead = mCanRead;
	}

	public static void write(String msg) {
		Calendar cal = Calendar.getInstance();

		String buf = String.format("[%02d:%02d:%02d] %s", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), msg);
		if (mJustPrompted) System.out.println();
		mJustPrompted = false;

		System.out.println(buf);
	}

	public static void write(Peer p, String msg) {
		write(String.format("(%02x:%s) %s", p.getPeerIndex(), p.getPeerId(), msg));
	}

	public static void error(String msg) {
		write("Error> " + msg);
	}

	public static void error(Peer p, String msg) {
		write(p, "Error> " + msg);
	}

	public String readLine() {
		String buf = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			buf = br.readLine();
		} catch (IOException e) { }

		return buf;
	}

	public Console() {
		Console.write("肄섏넄�뒪�젅�뱶 �깮�꽦.");

		this.mRunning = true;
		this.start();
	}

	@Override
	public void run() {
		boolean isPrompted = false;

		while (mRunning) {
			if (mCanRead) {
				if (!isPrompted) {
					System.out.print("Cmd> ");
					isPrompted = !isPrompted;
				}

				String buf = readLine();
				if (buf != null) {
					if (buf.length() > 0) {
						mCanRead = false;
						buf.replace('\t', ' ');
						String cmd[] = buf.split(" ");
						doCommand(cmd);
					}
					isPrompted = false;
				}
			}

			try { Thread.sleep(10); } catch (InterruptedException e) { }
		}
		Console.write("肄섏넄�뒪�젅�뱶 醫낅즺.");
	}

	private void doCommand(String[] cmd) {
		if (cmd[0].equalsIgnoreCase("quit")) {
			Console.write("�뵾�뼱瑜� 醫낅즺�빀�땲�떎.");
			Main.endWork();
			return;
		} else if (cmd[0].equalsIgnoreCase("register")) {
			if (!isCmdError(cmd, 2)) {
				Packet p = new Packet();
				p.setType(PacketHandler.TYPE_REGISTER_PEER);
				p.setOption((byte) 0);
				p.setPayload(cmd[1]);
				PeerManager.get(0).write(p);
				return;
			}
		} else if (cmd[0].equalsIgnoreCase("share")) {
			if (!isCmdError(cmd, 2)) {
				SharedFile f = new SharedFile(cmd[1]);
				if (f.getFileStatus() == FileManager.STATUS_NOT_EXISTS) {
					Console.error("�뙆�씪�쓣 李얠쓣 �닔 �뾾�뒿�땲�떎.");
					mCanRead = true;
				} else {
					FileManager.addFile(f);
					Packet p = new Packet();
					p.setType(PacketHandler.TYPE_REGISTER_FILE);
					p.setOption((byte) 0);
					p.setPayload(f.getFileName() + "\t" + f.getFileSize() + "\t" + f.getFileMD5());
					PeerManager.get(0).write(p);
				}
				return;
			}
		} else if (cmd[0].equalsIgnoreCase("show")) {
			if (!isCmdError(cmd, 2)) {
				if (cmd[1].equalsIgnoreCase("server")) {
					Console.write("�꽌踰� 二쇱냼: " + PeerManager.get(0).getRemoteAddress() + ":"  + PeerManager.get(0).getRemotePort());
					mCanRead = true;
					return;
				} else if (cmd[1].equalsIgnoreCase("peers")) {
					Packet p = new Packet();
					p.setType(PacketHandler.TYPE_LIST_PEERS);
					p.setOption((byte) 0);
					PeerManager.get(0).write(p);
					return;
				}
			}
		} else if (cmd[0].equalsIgnoreCase("find")) {
			if (!isCmdError(cmd, 3)) {
				Packet p = new Packet();
				p.setOption((byte) 0);
				p.setPayload(cmd[2]);
				if (cmd[1].equalsIgnoreCase("file")) {
					p.setType(PacketHandler.TYPE_SEARCH_FILE);
					PeerManager.get(0).write(p);
					return;
				} else if (cmd[1].equalsIgnoreCase("peer")) {
					p.setType(PacketHandler.TYPE_SEARCH_PEER);
					PeerManager.get(0).write(p);
					return;
				}
			}
		} else if (cmd[0].equalsIgnoreCase("get")) {
			if (!isCmdError(cmd, 4)) {
				if (FileManager.getFile(Integer.parseInt(cmd[1])) != null) {
					Console.error("�씠誘� 怨듭쑀以묒씤 �뙆�씪�엯�땲�떎.");
					mCanRead = true;
					return;
				}

				Socket c = new Socket();
				try {
					c.setTcpNoDelay(true);
					c.connect(new InetSocketAddress(cmd[2], Integer.parseInt(cmd[3])));
					//c = new Socket(cmd[2], Integer.parseInt(cmd[3]));
				} catch (NumberFormatException e) {
					Console.error("�옒紐� �맂 �룷�듃踰덊샇 �엯�땲�떎.");
				} catch (UnknownHostException e) {
					Console.error("�옒紐� �맂 �샇�뒪�듃 �엯�땲�떎.");
				} catch (IOException e) {
					Console.error("�긽�� �뵾�뼱�뿉 �젒�냽 �븷 �닔 �뾾�뒿�땲�떎.");
				}
				if (!c.isConnected()) { mCanRead = true; return; }

				Peer p = new Peer(PeerManager.popIndex(), c);
				PeerManager.add(p);

				Packet packet = new Packet();
				packet.setType(PacketHandler.TYPE_REQUEST_FILE);
				packet.setOption((byte) 0);
				packet.setPayload(cmd[1]);

				p.write(packet);
				return;
			}
		}

		Console.error("�옒紐삳맂 紐낅졊�엯�땲�떎.");
		mCanRead = true;
	}

	private boolean isCmdError(String[] cmd, int argc) {
		if (cmd.length < argc) {
			//Console.error("�옒紐삳맂 紐낅졊�엯�땲�떎.");
			return true;
		}

		return false;
	}
}
