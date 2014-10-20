package net.year4000.mapnodes.utils;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EnumProtocol;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketDataSerializer;
import net.minecraft.server.v1_7_R4.PacketListener;
import net.minecraft.util.com.google.common.collect.BiMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class PacketInjector {
    public static void inject() {
        try {
            addPacket( EnumProtocol.PLAY, true, 0x45, PacketTitle.class );
        } catch (IllegalAccessException | NoSuchFieldException e ) {
            e.printStackTrace();
        }
    }

    private static void addPacket(EnumProtocol protocol, boolean clientbound, int id, Class<? extends Packet> packet) throws NoSuchFieldException, IllegalAccessException {
        Field packets;

        if (!clientbound) {
            packets = EnumProtocol.class.getDeclaredField("h");
        }
        else {
            packets = EnumProtocol.class.getDeclaredField("i");
        }

        packets.setAccessible(true);
        BiMap<Integer, Class<? extends Packet>> pMap = (BiMap<Integer, Class<? extends Packet>>) packets.get(protocol);
        pMap.put( id, packet );
        Field map = EnumProtocol.class.getDeclaredField( "f" );
        map.setAccessible(true);
        Map<Class<? extends Packet>, EnumProtocol> protocolMap = (Map<Class<? extends Packet>, EnumProtocol>) map.get(null);
        protocolMap.put(packet, protocol);
    }

    public static class PacketTitle extends Packet {
        private Action action;

        // TITLE & SUBTITLE
        private IChatBaseComponent text;

        // TIMES
        private int fadeIn = -1;
        private int stay = -1;
        private int fadeOut = -1;

        public PacketTitle() {}

        public PacketTitle(Action action) {
            this.action = action;
        }

        public PacketTitle(Action action, IChatBaseComponent text) {
            this(action);
            this.text = text;
        }

        public PacketTitle(Action action, int fadeIn, int stay, int fadeOut) {
            this(action);
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }


        @Override
        public void a(PacketDataSerializer packetdataserializer) throws IOException {
            this.action = Action.values()[packetdataserializer.a()];

            switch (action) {
                case TITLE:
                case SUBTITLE:
                    this.text = ChatSerializer.a(packetdataserializer.c(32767));
                    break;
                case TIMES:
                    this.fadeIn = packetdataserializer.readInt();
                    this.stay = packetdataserializer.readInt();
                    this.fadeOut = packetdataserializer.readInt();
                    break;
            }
        }

        @Override
        public void b(PacketDataSerializer packetdataserializer) throws IOException {
            packetdataserializer.b(action.ordinal());

            switch (action) {
                case TITLE:
                case SUBTITLE:
                    packetdataserializer.a(ChatSerializer.a(this.text));
                    break;
                case TIMES:
                    packetdataserializer.writeInt(this.fadeIn);
                    packetdataserializer.writeInt(this.stay);
                    packetdataserializer.writeInt(this.fadeOut);
                    break;
            }
        }

        @Override
        public void handle(PacketListener packetlistener) {}

        public static enum Action {
            TITLE,
            SUBTITLE,
            TIMES,
            CLEAR,
            RESET
        }
    }
}