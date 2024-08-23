package com.pacscope.pacscope;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.EtherType;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayingPacketsInTable {
    public static String getSourceAddress(Packet packet){
        String srcAddr;
        String regx = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher;
        if(isNotEtherPacket(packet)){
            if(isLoopBackPacket(packet)){
                if(isLoopBackAndIpv4Packet(packet)){
                    IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                    srcAddr = ipV4Packet.getHeader().getSrcAddr().toString();
                    matcher = pattern.matcher(srcAddr);
                    if(matcher.find()){
                        srcAddr = matcher.group();
                    }
                }
                else if(isLoopBackAndIpv6Packet(packet)){
                    IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
                    srcAddr = ipV6Packet.getHeader().getSrcAddr().toString();
                    matcher = pattern.matcher(srcAddr);
                    if(matcher.find()){
                        srcAddr = matcher.group();
                    }
                }
                else{
                    srcAddr = "Unknown";
                }
            }
            else{
                srcAddr = "Unknown";
            }
        }
        else {
            EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
            if (isIPV4Packet(ethernetPacket)) {
                IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
                srcAddr = ipv4Packet.getHeader().getSrcAddr().getHostAddress();
            } else if (isIPV6Packet(ethernetPacket)) {
                IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
                srcAddr = ipV6Packet.getHeader().getSrcAddr().getHostAddress();
            } else {
                srcAddr = ethernetPacket.getHeader().getSrcAddr().toString();
            }
        }
        return  srcAddr;
    }


    public static String getDestinationAddress(Packet packet){
        String dstAddr;
        String regx = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher;
        if(isNotEtherPacket(packet)){
            if(isLoopBackPacket(packet)){
                if(isLoopBackAndIpv4Packet(packet)){
                    IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                    dstAddr = ipV4Packet.getHeader().getDstAddr().toString();
                    matcher = pattern.matcher(dstAddr);
                    if(matcher.find()){
                        dstAddr = matcher.group();
                    }
                }
                else if(isLoopBackAndIpv6Packet(packet)){
                    IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
                    dstAddr = ipV6Packet.getHeader().getDstAddr().toString();
                    matcher = pattern.matcher(dstAddr);
                    if(matcher.find()){
                        dstAddr = matcher.group();
                    }
                }
                else{
                    dstAddr = "Unknown";
                }
            }
            else{
                dstAddr = "Unknown";
            }
        }
        else {
            EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
            if (isIPV4Packet(ethernetPacket)) {
                IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
                dstAddr = ipv4Packet.getHeader().getDstAddr().getHostAddress();
            } else if (isIPV6Packet(ethernetPacket)) {
                IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
                dstAddr = ipV6Packet.getHeader().getDstAddr().getHostAddress();
            } else {
                dstAddr = ethernetPacket.getHeader().getDstAddr().toString();
            }
        }
        return dstAddr;
    }

    public static String getProtocolName(Packet packet){
        String protocolName;
        String regx = "[a-zA-Z]+\\d*";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher;
        if(isNotEtherPacket(packet)){
            if(isLoopBackPacket(packet)){
                if(isLoopBackAndIpv4Packet(packet)){
                    IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                    protocolName = ipV4Packet.getHeader().getProtocol().toString();
                    matcher = pattern.matcher(protocolName);
                    if(matcher.find()){
                        protocolName = matcher.group();
                    }
                    if(Objects.equals(protocolName, "ICMPv4")){
                        protocolName = "ICMP";
                    }
                }
                else if(isLoopBackAndIpv6Packet(packet)){
                    IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
                    protocolName = ipV6Packet.getHeader().getProtocol().toString();
                    matcher = pattern.matcher(protocolName);
                    if(matcher.find()){
                        protocolName = matcher.group();
                    }
                }
                else{
                    protocolName = packet.getHeader().toString();
                }
            }
            else{
                protocolName = packet.getHeader().getClass().toString();
            }
        }
        else {
            EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
            if (isIPV4Packet(ethernetPacket)) {
                IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
                if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "6 (TCP)")) {
                    TcpPacket tcpPacket = ipv4Packet.get(TcpPacket.class);
                    protocolName = identifyTCPProtocol(tcpPacket);
                } else if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "17 (UDP)")) {
                    UdpPacket udpPacket = ipv4Packet.get(UdpPacket.class);
                    protocolName = identifyUDPProtocol(udpPacket);

                } else if (Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "1 (ICMP)")) {
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
                } else if (Objects.equals(ipV6Packet.getHeader().getNextHeader().toString(), "58 (ICMPv6)")) {
                    protocolName = "ICMPv6";
                } else {
                    protocolName = "IPv6";
                }
            } else {
                protocolName = ethernetPacket.getHeader().getType().name();
            }
        }
        return protocolName;
    }

    private static boolean isLoopBackAndIpv6Packet(Packet packet) {
        return packet.getPayload() instanceof IpV6Packet;
    }

    private static boolean isLoopBackAndIpv4Packet(Packet packet) {
        return packet.getPayload() instanceof IpV4Packet;
    }

    private static boolean isIPV4Packet(EthernetPacket ethernetPacket){
        return  ethernetPacket.getHeader().getType() == EtherType.IPV4;
    }

    private static boolean isNotEtherPacket(Packet packet){
        return !packet.contains(EthernetPacket.class);
    }

    private static boolean isLoopBackPacket(Packet packet){
        if(packet instanceof BsdLoopbackPacket){
            return true;
        }
        else if(packet instanceof LinuxSllPacket){
            return true;
        }
        else if(packet instanceof RadiotapPacket){
            return true;
        }
        else if(packet instanceof IpV4Packet || packet instanceof IpV6Packet){
            return true;
        }
        else return false;

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
        } else if (Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "53 (Domain Name Server)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(), "53 (Domain Name Server)")) {
            protocolName = "DNS";
        }
        else {
            protocolName = "TCP";
        }
        return protocolName;
    }
    public static String identifyUDPProtocol(UdpPacket udpPacket){
        String protocolName;
        if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "53 (Domain Name Server)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "53 (Domain Name Server)")){
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
    public static boolean isValidFilter(String protocolFilter){
        return Objects.equals(protocolFilter, "TCP") || Objects.equals(protocolFilter, "IPv4") || Objects.equals(protocolFilter, "IPv6") || Objects.equals(protocolFilter, "ARP") || Objects.equals(protocolFilter, "ICMP") || Objects.equals(protocolFilter, "ICMPv6") || Objects.equals(protocolFilter, "HTTP") || Objects.equals(protocolFilter, "HTTPS") || Objects.equals(protocolFilter, "UDP") || Objects.equals(protocolFilter, "FTP") || Objects.equals(protocolFilter, "FTPS") || Objects.equals(protocolFilter, "IMAP") || Objects.equals(protocolFilter, "IMAPS") || Objects.equals(protocolFilter, "SMTP") || Objects.equals(protocolFilter, "POP3") || Objects.equals(protocolFilter, "POP3S") || Objects.equals(protocolFilter, "SSH") || Objects.equals(protocolFilter, "Telnet") || Objects.equals(protocolFilter, "SNMP") || Objects.equals(protocolFilter, "NTP") || Objects.equals(protocolFilter, "VoIP") || Objects.equals(protocolFilter, "DHCP") || Objects.equals(protocolFilter, "mDNS") || Objects.equals(protocolFilter, "DNS");
    }
    public static void generateAlertInvalidFilter(String filter){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid Filter");
            alert.setHeaderText(null);
            alert.setContentText(filter + " is an invalid filter!");
            alert.show();
        });
    }
}