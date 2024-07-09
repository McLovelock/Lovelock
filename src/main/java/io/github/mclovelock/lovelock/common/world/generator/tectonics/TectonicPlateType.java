package io.github.mclovelock.lovelock.common.world.generator.tectonics;

public enum TectonicPlateType {

    CONTINENTAL_PLATE(64),
    OCEANIC_PLATE(0),

    FAKE_PLATE_BORDER_TYPE(-1);

    private int averageElevation;

    private TectonicPlateType(int averageElevation) {
        this.averageElevation = averageElevation;
    }

    public int getAverageElevation() {
        return averageElevation;
    }

}
