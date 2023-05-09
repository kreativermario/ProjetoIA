package algoGenetico;

import java.util.Objects;

public class Hyperparameters implements Comparable<Hyperparameters>{
    private int nrGenerations;
    private double elitismRatio;
    private int tournamentSize;
    private int seed;
    private int hiddenDimSize;
    private double mutationProb;
    private int populationSize;
    private double fitness;
    public static final int CHROMOSSOME_SIZE = 8;

    public Hyperparameters(int nrGenerations, double elitismRatio, int tournamentSize, int seed, int hiddenDimSize,
                           double mutationProb, int populationSize){
        this.nrGenerations = nrGenerations;
        this.elitismRatio = elitismRatio;
        this.tournamentSize = tournamentSize;
        this.seed = seed;
        this.hiddenDimSize = hiddenDimSize;
        this.mutationProb = mutationProb;
        this.populationSize = populationSize;
    }

    public Hyperparameters(double [] chromossome) {
        setChromosome(chromossome);
    }

    public double[] getChromossome() {
        return new double[] {
                nrGenerations,
                elitismRatio,
                tournamentSize,
                seed,
                hiddenDimSize,
                mutationProb,
                populationSize,
                fitness
        };
    }

    public void setChromosome(double[] chromosome) {
        if (chromosome.length != CHROMOSSOME_SIZE) {
            throw new IllegalArgumentException("Invalid chromosome size: " + chromosome.length);
        }
        this.nrGenerations = (int) chromosome[0];
        this.elitismRatio = (double) chromosome[1];
        this.tournamentSize = (int) chromosome[2];
        this.seed = (int) chromosome[3];
        this.hiddenDimSize = (int) chromosome[4];
        this.mutationProb = chromosome[5];
        this.populationSize = (int) chromosome[6];
        this.fitness = chromosome[7];
    }


    public int getNrGenerations() {
        return nrGenerations;
    }

    public double getElitismRatio() {
        return elitismRatio;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public int getSeed() {
        return seed;
    }

    public int getHiddenDimSize() {
        return hiddenDimSize;
    }

    public double getMutationProb() {
        return mutationProb;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

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

    @Override
    public int compareTo(Hyperparameters o) {
        return Double.compare(o.fitness, this.fitness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hyperparameters that = (Hyperparameters) o;
        return nrGenerations == that.nrGenerations && elitismRatio == that.elitismRatio
                && tournamentSize == that.tournamentSize && seed == that.seed && hiddenDimSize == that.hiddenDimSize
                && Double.compare(that.mutationProb, mutationProb) == 0 && populationSize == that.populationSize
                && Double.compare(that.fitness, fitness) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nrGenerations, elitismRatio, tournamentSize,
                seed, hiddenDimSize, mutationProb, populationSize, fitness);
    }
}

