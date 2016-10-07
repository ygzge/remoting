package org.jetlang.remote.example.ws;

import org.jetlang.fibers.NioControls;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class WebSocketConnection {

    private static final byte OPCODE_CONT = 0x0;
    private static final byte OPCODE_TEXT = 0x1;
    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;
    private static final byte OPCODE_PING = 0x9;
    private static final byte OPCODE_PONG = 0xA;
    public static final byte[] empty = new byte[0];
    private final SocketChannel channel;
    private final NioControls controls;
    private final Charset charset;

    public WebSocketConnection(HttpRequest headers, SocketChannel channel, NioControls controls, Charset charset) {
        this.channel = channel;
        this.controls = controls;
        this.charset = charset;
    }

    public void send(String msg) {
        final byte[] bytes = msg.getBytes(charset);
        send(OPCODE_TEXT, bytes);
    }

    private void send(byte opCode, byte[] bytes) {
        final int length = bytes.length;
        byte header = 0;
        header |= 1 << 7;
        header |= opCode % 128;
        ByteBuffer bb = NioReader.bufferAllocate(2 + length);
        bb.put(header);
        bb.put((byte) length);
        if (bytes.length > 0) {
            bb.put(bytes);
        }
        bb.flip();
        controls.write(channel, bb);
    }

    void sendClose() {
        send(OPCODE_CLOSE, empty);
    }
}
