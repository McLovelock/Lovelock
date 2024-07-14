package io.github.mclovelock.lovelock.common.world.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicChunk;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicPlate;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicsGenerationHandler;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LovelockChunkGenerator extends ChunkGenerator {

    public static final MapCodec<LovelockChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(generator -> generator.biome),
                    Codec.LONG.fieldOf("seed").stable().orElse(System.nanoTime()).forGetter(generator -> generator.seed)
                ).apply(instance, instance.stable(LovelockChunkGenerator::new))
    );

    private final TectonicsGenerationHandler tectonicsGenerationHandler;

    private final RegistryEntry<Biome> biome;
    private final long seed;

    private LovelockChunkGenerator(RegistryEntry<Biome> biome, long seed) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
        this.seed = seed;

        tectonicsGenerationHandler = new TectonicsGenerationHandler(this.seed);
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
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockState(mutable.set(x, chunk.getBottomY(), z), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
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
                int worldX = chunk.getPos().getStartX() + x;
                int worldZ = chunk.getPos().getStartZ() + z;

                TectonicChunk tChunk = tectonicsGenerationHandler.getTectonicPlateAt(worldX, worldZ);
                var plate = tChunk.getAssociatedPlate();
                BlockState blockState = plate.isOceanic() ? Blocks.WATER.getDefaultState() : Blocks.MOSS_BLOCK.getDefaultState();

                if ((worldX == tChunk.getSeedX()) && (worldZ == tChunk.getSeedZ()))
                    blockState = Blocks.COAL_BLOCK.getDefaultState();

                for (int y = 0; y < 4; y++) {
                    int yChunk = chunk.getBottomY() + y + 1;
                    chunk.setBlockState(mutable.set(x, yChunk, z), blockState, false);
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
        return -64;
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

}
