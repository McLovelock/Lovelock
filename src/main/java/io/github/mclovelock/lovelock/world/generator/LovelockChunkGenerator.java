package io.github.mclovelock.lovelock.world.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LovelockChunkGenerator extends ChunkGenerator {

    public static final MapCodec<LovelockChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(LovelockChunkGenerator::getBiome))
                    .apply(instance, instance.stable(LovelockChunkGenerator::new))
    );

    private final List<BlockState> layerBlocks = new ArrayList<>();

    private final RegistryEntry<Biome> biome;

    public LovelockChunkGenerator(RegistryEntry<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
        for (int i = 0; i < 1; i++) {
            layerBlocks.add(Blocks.PRISMARINE.getDefaultState());
        }
    }

    @Override
    public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper<StructureSet> structureSetRegistry, NoiseConfig noiseConfig, long seed) {
        Stream<RegistryEntry<StructureSet>> stream = structureSetRegistry.streamEntries().map(reference -> reference);
        return StructurePlacementCalculator.create(noiseConfig, seed, this.biomeSource, stream);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return world.getBottomY() + Math.min(world.getHeight(), 1);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        List<BlockState> list = layerBlocks;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int i = 0; i < Math.min(chunk.getHeight(), list.size()); i++) {
            BlockState blockState = (BlockState) list.get(i);
            if (blockState != null) {
                int j = chunk.getBottomY() + i;

                for (int k = 0; k < 16; k++) {
                    for (int l = 0; l < 16; l++) {
                        chunk.setBlockState(mutable.set(k, j, l), blockState, false);
                        heightmap.trackUpdate(k, j, l, blockState);
                        heightmap2.trackUpdate(k, j, l, blockState);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        List<BlockState> list = layerBlocks;

        for (int i = Math.min(list.size(), world.getTopY()) - 1; i >= 0; i--) {
            BlockState blockState = (BlockState)list.get(i);
            if (blockState != null && heightmap.getBlockPredicate().test(blockState)) {
                return world.getBottomY() + i + 1;
            }
        }

        return world.getBottomY();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(
                world.getBottomY(),
                (BlockState[])layerBlocks
                        .stream()
                        .limit((long)world.getHeight())
                        .map(state -> state == null ? Blocks.AIR.getDefaultState() : state)
                        .toArray(BlockState[]::new)
        );
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public void carve(
            ChunkRegion chunkRegion,
            long seed,
            NoiseConfig noiseConfig,
            BiomeAccess biomeAccess,
            StructureAccessor structureAccessor,
            Chunk chunk,
            GenerationStep.Carver carverStep
    ) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }

    public RegistryEntry<Biome> getBiome() {
        return biome;
    }

}
