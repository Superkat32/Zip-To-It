package net.superkat.ziptoit.network.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.superkat.ziptoit.ZipToIt;

public record WallStickStartCommonPacket(int playerId, Vec3d pos, BlockPos wallPos) implements CustomPayload {
    public static final Identifier WALL_STICK_START_ID = Identifier.of(ZipToIt.MOD_ID, "wall_stick_start");
    public static final CustomPayload.Id<WallStickStartCommonPacket> ID = new CustomPayload.Id<>(WALL_STICK_START_ID);
    public static final PacketCodec<RegistryByteBuf, WallStickStartCommonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WallStickStartCommonPacket::playerId,
            Vec3d.PACKET_CODEC, WallStickStartCommonPacket::pos,
            BlockPos.PACKET_CODEC, WallStickStartCommonPacket::wallPos,
            WallStickStartCommonPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
