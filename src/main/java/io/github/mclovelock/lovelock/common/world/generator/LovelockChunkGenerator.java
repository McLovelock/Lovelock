package io.github.mclovelock.lovelock.common.world.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mclovelock.lovelock.Lovelock;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicPlate;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicsGenerationHandler;
import net.minecraft.block.Block;
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
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LovelockChunkGenerator extends ChunkGenerator {

    public static final MapCodec<LovelockChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(LovelockChunkGenerator::getBiome))
                    .apply(instance, instance.stable(LovelockChunkGenerator::new))
    );

    private final TectonicsGenerationHandler tectonicsGenerationHandler;

    private final RegistryEntry<Biome> biome;

    public LovelockChunkGenerator(RegistryEntry<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;

        tectonicsGenerationHandler = new TectonicsGenerationHandler();
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
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                TectonicPlate plate = tectonicsGenerationHandler.getTectonicPlateAt(x, z);

                BlockState blockState = plate == null || plate.isOceanic() ? Blocks.WATER.getDefaultState() : Blocks.GRASS_BLOCK.getDefaultState();

                for (int y = 0; y < 2; y++) {
                    int yChunk = chunk.getBottomY() + y + 1;
                    chunk.setBlockState(mutable.set(x, yChunk, z), y == 0 ? Blocks.BEDROCK.getDefaultState() : blockState, false);
                    heightmap.trackUpdate(x, yChunk, z, blockState);
                    heightmap2.trackUpdate(x, yChunk, z, blockState);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 2;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(
                world.getBottomY(),
                new BlockState[] { Blocks.STONE.getDefaultState() }
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
