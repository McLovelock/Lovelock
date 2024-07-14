package io.github.mclovelock.lovelock.common.world.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mclovelock.lovelock.Lovelock;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicChunk;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicPlate;
import io.github.mclovelock.lovelock.common.world.generator.tectonics.TectonicsGenerationHandler;
import io.github.mclovelock.lovelock.utils.maths.Maths;
import io.github.mclovelock.lovelock.utils.maths.geometry.LineEquation;
import io.github.mclovelock.lovelock.utils.maths.voronoi.Edge;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiCell;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiContext;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiSite;
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
import org.joml.Vector2d;

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
                int worldX = chunk.getPos().getStartX() + x;
                int worldZ = chunk.getPos().getStartZ() + z;

                //VoronoiContext vc = tectonicsGenerationHandler.getTectonicPlateAt(worldX, worldZ);
                //TectonicChunk tChunk = tectonicsGenerationHandler.getTectonicChunk(worldX, worldZ);
                TectonicChunk tChunk = tectonicsGenerationHandler.getTectonicPlateAt(worldX, worldZ);
                VoronoiContext vc = tChunk.getVoronoiGraph();
                TectonicPlate plate = tChunk.getAssociatedPlate();

                BlockState blockState = plate.isOceanic() ? Blocks.WATER.getDefaultState() : Blocks.GRASS_BLOCK.getDefaultState();

                if ((worldX == tChunk.siteX()) && (worldZ == tChunk.siteY()))
                    blockState = Blocks.COAL_BLOCK.getDefaultState();

                cells:
                for (VoronoiCell cell : vc.getCells()) {
                    for (Edge edge : cell.getEdges()) {
                        VoronoiSite v0 = vc.getVertices()[edge.a()];
                        VoronoiSite v1 = vc.getVertices()[edge.b()];

                        var line = new LineEquation(v0.siteX(), v0.siteY(), v1.siteX(), v1.siteY());

                        double at = line.at(worldX + 0.5, worldZ + 0.5);
                        if (Math.abs(at) < 2.5) {
                            blockState = Blocks.REDSTONE_BLOCK.getDefaultState();
                            break cells;
                        }
                    }
                }

                for (VoronoiSite v : vc.getVertices()) {
                    if ((worldX == (int)v.siteX()) && (worldZ == (int)v.siteY()))
                        blockState = Blocks.EMERALD_BLOCK.getDefaultState();
                }

                for (int y = 0; y < 2; y++) {
                    int yChunk = chunk.getBottomY() + y;
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
