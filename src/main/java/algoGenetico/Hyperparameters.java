package algoGenetico;

public class Hyperparameters {
    private int nrGenerations = 40;
    private int nrFitIndividuals = 20;
    private int tournamentSize = 4;
    private int seed = 750;
    private int hiddenDimSize = 15;
    private double mutationProb = 0.3;
    private int populationSize = 650;

    public int getNrGenerations() {
        return nrGenerations;
    }

    public void setNrGenerations(int nrGenerations) {
        this.nrGenerations = nrGenerations;
    }

    public int getNrFitIndividuals() {
        return nrFitIndividuals;
    }

    public void setNrFitIndividuals(int nrFitIndividuals) {
        this.nrFitIndividuals = nrFitIndividuals;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getHiddenDimSize() {
        return hiddenDimSize;
    }

    public void setHiddenDimSize(int hiddenDimSize) {
        this.hiddenDimSize = hiddenDimSize;
    }

    public double getMutationProb() {
        return mutationProb;
    }

    public void setMutationProb(double mutationProb) {
        this.mutationProb = mutationProb;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public String toString() {
        return "Hyperparameters{" +
                "nrGenerations=" + nrGenerations +
                ", nrFitIndividuals=" + nrFitIndividuals +
                ", tournamentSize=" + tournamentSize +
                ", seed=" + seed +
                ", hiddenDimSize=" + hiddenDimSize +
                ", mutationProb=" + mutationProb +
                ", populationSize=" + populationSize +
                '}';
    }
}

