package com.pacscope.pacscope;

public class ListedPackets {
    private String number;
    private String source;
    private String destination;
    private String protocol;
    private String length;
    private String info;

    public ListedPackets(String number, String source, String destination, String protocol, String length, String info) {
        this.number = number;
        this.source = source;
        this.destination = destination;
        this.protocol = protocol;
        this.length = length;
        this.info = info;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String packetNumber) {
        this.number = packetNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
