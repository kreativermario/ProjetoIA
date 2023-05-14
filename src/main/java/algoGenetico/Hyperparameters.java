package algoGenetico;


public record Hyperparameters(int nrGenerations, double elitismRatio, int tournamentSize, int seed, int hiddenDimSize,
                              double mutationProb, int populationSize) {

    @Override
    public String toString() {
        return "Hyperparameters{" +
                "nrGenerations=" + nrGenerations +
                ", elitismRatio=" + elitismRatio +
                ", tournamentSize=" + tournamentSize +
                ", seed=" + seed +
                ", hiddenDimSize=" + hiddenDimSize +
                ", mutationProb=" + mutationProb +
                ", populationSize=" + populationSize +
                '}';
    }

}

