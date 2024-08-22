package com.pacscope.pacscope;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.EtherType;
import java.util.Objects;

public class DisplayingPacketsInTable {
    public static String getSourceAddress(EthernetPacket ethernetPacket){
        String srcAddr;
        if (isIPV4Packet(ethernetPacket)) {
            IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
            srcAddr = ipv4Packet.getHeader().getSrcAddr().getHostAddress();
        } else if (isIPV6Packet(ethernetPacket)) {
            IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
            srcAddr = ipV6Packet.getHeader().getSrcAddr().getHostAddress();
        }
        else{
            srcAddr = ethernetPacket.getHeader().getSrcAddr().toString();
        }
        return  srcAddr;
    }


    public static String getDestinationAddress(EthernetPacket ethernetPacket){
        String dstAddr;
        if (isIPV4Packet(ethernetPacket)) {
            IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
            dstAddr = ipv4Packet.getHeader().getDstAddr().getHostAddress();
        } else if (isIPV6Packet(ethernetPacket)) {
            IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
            dstAddr = ipV6Packet.getHeader().getDstAddr().getHostAddress();
        }
        else{
            dstAddr = ethernetPacket.getHeader().getDstAddr().toString();
        }
        return dstAddr;
    }

    public static String getProtocolName(EthernetPacket ethernetPacket){
        String protocolName;
        if (isIPV4Packet(ethernetPacket)) {
            IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
            if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "6 (TCP)")) {
                TcpPacket tcpPacket = ipv4Packet.get(TcpPacket.class);
                protocolName = identifyTCPProtocol(tcpPacket);
            } else if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "17 (UDP)")) {
                UdpPacket udpPacket = ipv4Packet.get(UdpPacket.class);
                protocolName = identifyUDPProtocol(udpPacket);

            } else if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "1 (ICMP")) {
                protocolName = "ICMP";
            } else {
                protocolName = "IPv4";
            }
        } else if (isIPV6Packet(ethernetPacket)) {
            IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
            if (ipV6Packet.getHeader().getProtocol().toString().equals("6 (TCP)")) {
                TcpPacket tcpPacket = ipV6Packet.get(TcpPacket.class);
                protocolName = identifyTCPProtocol(tcpPacket);
            } else if (Objects.equals(ipV6Packet.getHeader().getProtocol().toString(), "17 (UDP)")) {
                UdpPacket udpPacket = ipV6Packet.get(UdpPacket.class);
                protocolName = identifyUDPProtocol(udpPacket);
            } else if (Objects.equals(ipV6Packet.getHeader().getProtocol().toString(), "1 (ICMP")) {
                protocolName = "ICMPv6";
            } else {
                protocolName = "IPv6";
            }
        }
        else{
            protocolName = ethernetPacket.getHeader().getType().name();
        }
        return protocolName;
    }

    private static boolean isIPV4Packet(EthernetPacket ethernetPacket){
        return  ethernetPacket.getHeader().getType() == EtherType.IPV4;
    }

    private static boolean isIPV6Packet(EthernetPacket ethernetPacket){
        return  ethernetPacket.getHeader().getType() == EtherType.IPV6;
    }

    private static String identifyTCPProtocol(TcpPacket tcpPacket){
        String protocolName;
        if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "443 (HTTPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(), "443 (HTTPS)")){
            protocolName = "HTTPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "80 (HTTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(), "80 (HTTP)")){
            protocolName = "HTTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "25 (SMTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"25 (SMTP)")){
            protocolName = "SMTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "143 (IMAP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"143 (IMAP)")){
            protocolName = "IMAP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "993 (IMAPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"993 (IMAPS)")){
            protocolName = "IMAPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "110 (POP3)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"110 (POP3)")){
            protocolName = "POP3";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "995 (POP3S)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"995 (POP3S)")){
            protocolName = "POP3S";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "21 (FTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"21 (FTP)") || Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "20 (FTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"20 (FTP)")){
            protocolName = "FTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "990 (FTPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"990 (FTPS)")){
            protocolName = "FTPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "22 (SSH)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"22 (SSH)")){
            protocolName = "SSH";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "23 (Telnet)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"23 (Telnet)")){
            protocolName = "Telnet";
        }
        else {
            protocolName = "TCP";
        }
        return protocolName;
    }
    public static String identifyUDPProtocol(UdpPacket udpPacket){
        String protocolName;
        if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "53 (DNS)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "53 (DNS)")){
            protocolName = "DNS";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "161 (SNMP)") ||Objects.equals(udpPacket.getHeader().getDstPort().toString(), "161 (SNMP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "162") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "162")){
            protocolName = "SNMP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "123 (NTP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "123 (NTP)")){
            protocolName = "NTP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5060 (VoIP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5060 (VoIP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5061 (VoIP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5061 (VoIP)")){
            protocolName = "VoIP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "68 (DHCP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "67 (DHCP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "68 (DHCP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "67 (DHCP)")){
            protocolName = "DHCP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5353 (mDNS)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5353 (mDNS)")){
            protocolName = "mDNS";
        }
        else{
            protocolName = "UDP";
        }
        return protocolName;
    }
}

