package com.lgcns.suno.net.p2p.server.packets;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.lgcns.suno.net.p2p.server.p2pserver.Console;
import com.lgcns.suno.net.p2p.server.peers.Peer;
import com.lgcns.suno.net.p2p.server.peers.PeerManager;
import com.lgcns.suno.net.p2p.server.sharing.FileManager;
import com.lgcns.suno.net.p2p.server.sharing.SharedFile;

public class PacketHandler {
	public static final byte TYPE_CHECK_PEER				= 0;
	public static final byte TYPE_HEART_BEAT				= 2;
	public static final byte TYPE_REGISTER_PEER			= 3;
	public static final byte TYPE_REGISTER_FILE			= 4;
	public static final byte TYPE_SEARCH_FILE				= 5;
	public static final byte TYPE_SEARCH_PEER			= 6;
	public static final byte TYPE_REQUEST_FILE			= 7;
	public static final byte TYPE_TRANSFER_FILE			= 8;
	public static final byte TYPE_LIST_PEERS				= 9;
	public static final byte TYPE_RELAY_PEERS				= 10;

	public static final byte TYPE_GENERAL_MESSAGE	= 100;

	public static final byte GMSG_INVALID_REQUEST				= 0;
	public static final byte GMSG_NOT_REGISTERED				= 1;
	public static final byte GMSG_HOLE_PUNCHING_FAILED		= 2;
	public static final byte GMSG_NAME_IN_USE						= 3;
	public static final byte GMSG_INVALID_NAME						= 4;
	public static final byte GMSG_FILE_REGISTER_FAILED		= 5;
	public static final byte GMSG_SEARCH_FILE_FAILED			= 6;
	public static final byte GMSG_SEARCH_PEER_FAILED			= 7;
	public static final byte GMSG_FILE_NOT_FOUND					= 8;

	public static void doPacket(Peer peer, Packet packet) {
		String msg[];
		Hashtable<Peer, Vector<SharedFile>> tHashtable = new Hashtable<Peer, Vector<SharedFile>>();
		//System.out.println("Packet: " + "(t: " + packet.getType() + "), (o: " + packet.getOption() + ") " + packet.getPayload());

		switch (packet.getType()) {
		case TYPE_CHECK_PEER:
			msg = (packet.getPayload()).split(":");
			if (!validate(peer, msg, 2)) return;
			if (msg[0].equals(peer.getRemoteAddress())
					&& msg[1].equals(String.format("%d", peer.getRemotePort()))) {
				packet.setOption((byte) 1);
				peer.setLeaderPeer(true);
			}
			packet.setPayload(peer.getRemoteAddress() + ":" + peer.getRemotePort());
			peer.write(packet);
			if (peer.isLeaderPeer()) Console.write(peer, "由щ뜑�뵾�뼱�엯�땲�떎.");
			else Console.write(peer, "�씪諛섑뵾�뼱�엯�땲�떎.");
			break;
		case TYPE_HEART_BEAT:
			peer.setLastHeartbeat(Calendar.getInstance().getTimeInMillis());
			//peer.write(packet);
			//Console.write(peer, "Heartbeat �닔�떊");
			break;
		case TYPE_REGISTER_PEER:
			if (peer.isInitialized()) {
				packet.setType(TYPE_GENERAL_MESSAGE);
				packet.setOption(GMSG_INVALID_REQUEST);
			} else {
				switch (PeerManager.checkName(packet.getPayload())) {
				case 0:
					peer.setPeerId(packet.getPayload());
					packet.setOption((byte) 1);
					Console.write(peer, "�궗�슜�옄 �벑濡�: " + packet.getPayload());
					break;
				case 1:
					packet.setType(TYPE_GENERAL_MESSAGE);
					packet.setOption(GMSG_NAME_IN_USE);
					break;
				case 2:
					packet.setType(TYPE_GENERAL_MESSAGE);
					packet.setOption(GMSG_INVALID_NAME);
					break;
				}
			}
			packet.setPayload("");
			peer.write(packet);
			break;
		case TYPE_REGISTER_FILE:
			if (!isRegistered(peer)) return;
			msg = (packet.getPayload()).split("\t");
			if (!validate(peer, msg, 3)) return;
			SharedFile f = new SharedFile(msg[0], Long.parseLong(msg[1]), msg[2]);
			FileManager.addFile(peer, f);
			Console.write(peer, "怨듭쑀 異붽�: " + msg[0]);
			packet.setOption((byte) 1);
			packet.setPayload(f.getFileName() + "\t" + f.getFileId());
			peer.write(packet);
			break;
		case TYPE_SEARCH_FILE:
			if (isRegistered(peer)) {
				StringBuilder sb = new StringBuilder();
				FileManager.findFile(packet.getPayload(), tHashtable);
				for (Peer p: tHashtable.keySet())
					for (SharedFile tf: tHashtable.get(p)) {
						sb.append(tf.getFileId() + "\t" + tf.getFileName() + "\t" + tf.getFileSize2() + "\n");
					}
				if (sb.length() > 0) {
					packet.setOption((byte) 1);
					packet.setPayload(sb.toString());
					peer.write(packet);
				} else {
					packet.setType(TYPE_GENERAL_MESSAGE);
					packet.setOption(GMSG_SEARCH_FILE_FAILED);
					packet.setPayload("");
					peer.write(packet);
				}
			}
			break;
		case TYPE_SEARCH_PEER:
			if (isRegistered(peer)) {
				FileManager.findFile(Integer.parseInt(packet.getPayload()), tHashtable);
				Peer leadp = null, normp = null;

				int ld = Integer.MAX_VALUE;
				for (Peer tp: tHashtable.keySet())
					if (tp.getLoads() < ld) {
						if (tp.isLeaderPeer() || tp.isHolePunched())
							leadp = tp;
						else
							normp = tp;
						ld = tp.getLoads();
					}

				if (leadp == null && normp != null) {
					leadp = PeerManager.getMinLoad(peer);
					if (leadp != null) {
//						packet.setType(TYPE_RELAY_PEERS);
//						packet.setOption((byte) 0);
//						leadp.write(packet);

						packet.setOption((byte) 1);
						packet.setPayload(packet.getPayload() + "\t" + leadp.getRemoteAddress() + "\t" + leadp.getRemotePort());
						leadp.write(packet);
					}
				}

				if (leadp != null) {
					packet.setOption((byte) 1);
					packet.setPayload(packet.getPayload() + "\t" + leadp.getRemoteAddress() + "\t" + leadp.getRemotePort());
					peer.write(packet);
					return;
				}

				packet.setType(TYPE_GENERAL_MESSAGE);
				packet.setOption(GMSG_SEARCH_PEER_FAILED);
				packet.setPayload("");
				peer.write(packet);
			}
			break;
		case TYPE_REQUEST_FILE:
			msg = (packet.getPayload()).split("\t");
			Console.write(peer, "�쟾�넚 �슂泥�: " + msg[0] + " to " + msg[1] + ":" + msg[2]);
			break;
		case TYPE_TRANSFER_FILE:
			msg = (packet.getPayload()).split("\t");
			if (packet.getOption() == 0) {
				FileManager.findFile(Integer.parseInt(msg[0]), tHashtable);
				SharedFile tf = null;
				for (Peer tp: tHashtable.keySet()) { tf = tHashtable.get(tp).get(0); break; }
				Console.write(peer, "�쟾�넚 �떆�옉: " + "(" + msg[0] + ":" + tf.getFileName() + " to " + msg[1] + ":" + msg[2] + "(" + tf.getFileSize() + " bytes)");
				peer.setLoads(peer.getLoads() + 1);
			} else if (packet.getOption() == 2) {
				FileManager.findFile(Integer.parseInt(msg[0]), tHashtable);
				SharedFile tf = null;
				for (Peer tp: tHashtable.keySet()) { tf = tHashtable.get(tp).get(0); break; }
				Console.write(peer, "�쟾�넚 醫낅즺: " + "(" + msg[0] + ":" + tf.getFileName() + " to " + msg[1] + ":" + msg[2] + "(" + tf.getFileSize() + " bytes)");
				peer.setLoads(peer.getLoads() - 1);
			}
			break;
		case TYPE_LIST_PEERS:
			if (isRegistered(peer)) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < PeerManager.size(); i++) {
					Peer p = PeerManager.get(i);
					if (p.isInitialized())
						sb.append(p.getPeerId() + "\t" + p.getRemoteAddress() + "\t" + p.getRemotePort() + "\n");
				}
				packet.setOption((byte) 1);
				packet.setPayload(sb.toString());
				peer.write(packet);
			}
			break;
		case TYPE_RELAY_PEERS:
			msg = (packet.getPayload()).split("\t");
			FileManager.findFile(Integer.parseInt(msg[1]), tHashtable);
			Peer p = null;
			int ld = Integer.MAX_VALUE;
			for (Peer tp: tHashtable.keySet())
				if (tp.getLoads() < ld) {
					p = tp;
					ld = tp.getLoads();
				}

			if (p == null) {
				packet.setOption((byte) 1);
				peer.write(packet);
			} else {
				String fileid = msg[1];
				String auth = RelayManager.addRelay(p);
				packet.setOption((byte) 0);
				packet.setPayload(msg[0] + "\t" + auth);
				peer.write(packet);

				packet.setOption((byte) 2);
				packet.setPayload(auth + "\t" + fileid + "\t" + peer.getRemoteAddress() + "\t" + peer.getRemotePort());
				p.write(packet);
			}
			break;
		default:
			Console.error(peer, "�븣 �닔 �뾾�뒗 �뙣�궥�쓣 �쟾�떖諛쏆븯�뒿�땲�떎.");
		}
	}

	public static boolean isRegistered(Peer p) {
		if (p.isInitialized()) return true;

		Packet pk = new Packet();
		pk.setType(PacketHandler.TYPE_GENERAL_MESSAGE);
		pk.setOption(PacketHandler.GMSG_NOT_REGISTERED);
		p.write(pk);

		return false;
	}

	public static boolean validate(Peer peer, String[] msg, int argc) {
		if (msg.length < argc) {
			Packet p = new Packet();
			p.setType(TYPE_GENERAL_MESSAGE);
			p.setOption(GMSG_INVALID_REQUEST);
			peer.write(p);

			return false;
		}

		return true;
	}
}
