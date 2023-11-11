import java.util.Random;

public class PercolationSimulation {
    private boolean[][] grid;
    private int[][] size;
    private int openSites;
    private final int n;
    private final int virtualTop;
    private final int virtualBottom;

    public PercolationSimulation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Grid size must be greater than 0");
        }
        this.n = n;
        this.grid = new boolean[n][n];
        this.size = new int[n][n];
        this.openSites = 0;
        this.virtualTop = n * n;
        this.virtualBottom = n * n + 1;

        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
                size[i][j] = 1;
            }
        }
    }

    public void open(int row, int col) {
        validateIndices(row, col);

        if (!grid[row][col]) {
            grid[row][col] = true;
            openSites++;

            int current = getIndex(row, col);
            int above = getIndex(row - 1, col);
            int below = getIndex(row + 1, col);
            int left = getIndex(row, col - 1);
            int right = getIndex(row, col + 1);

            if (row == 0) {
                union(current, virtualTop);
            }
            if (row == n - 1) {
                union(current, virtualBottom);
            }

            if (row > 0 && isOpen(row - 1, col)) {
                union(current, above);
            }
            if (row < n - 1 && isOpen(row + 1, col)) {
                union(current, below);
            }
            if (col > 0 && isOpen(row, col - 1)) {
                union(current, left);
            }
            if (col < n - 1 && isOpen(row, col + 1)) {
                union(current, right);
            }
        }
    }

    public boolean isOpen(int row, int col) {
        validateIndices(row, col);
        return grid[row][col];
    }

    public boolean isFull(int row, int col) {
        validateIndices(row, col);
        return isOpen(row, col) && connected(getIndex(row, col), virtualTop);
    }

    public boolean percolates() {
        return connected(virtualTop, virtualBottom);
    }

    public double runMonteCarloSimulation(int trials) {
        if (trials <= 0) {
            throw new IllegalArgumentException("Number of trials must be greater than 0");
        }

        Random random = new Random();
        int openSitesAtThreshold = 0;

        for (int i = 0; i < trials; i++) {
            PercolationSimulation simulation = new PercolationSimulation(n);

            while (!simulation.percolates()) {
                int row = random.nextInt(n);
                int col = random.nextInt(n);

                simulation.open(row, col);
            }

            openSitesAtThreshold += simulation.openSites;
        }

        return (double) openSitesAtThreshold / (trials * n * n);
    }

    private int getIndex(int row, int col) {
        return row * n + col;
    }

    private void validateIndices(int row, int col) {
        if (row < 0 || row >= n || col < 0 || col >= n) {
            throw new IllegalArgumentException("Indices are out of bounds");
        }
    }

    private boolean connected(int p, int q) {
        return root(p) == root(q);
    }

    private void union(int p, int q) {
        int rootP = root(p);
        int rootQ = root(q);

        if (rootP == rootQ) {
            return;
        }

        if (size[rootP / n][rootP % n] < size[rootQ / n][rootQ % n]) {
            size[rootQ / n][rootQ % n] += size[rootP / n][rootP % n];
            grid[rootP / n][rootP % n] = true;
            rootP = rootQ;
        } else {
            size[rootP / n][rootP % n] += size[rootQ / n][rootQ % n];
            grid[rootQ / n][rootQ % n] = true;
        }
    }

    private int root(int i) {
        while (i != getIndex(i / n, i % n)) {
            i = getIndex(i / n, i % n);
        }
        return i;
    }

    public static void main(String[] args) {
        int n = 20;
        int trials = 1000;

        PercolationSimulation percolation = new PercolationSimulation(n);
        double threshold = percolation.runMonteCarloSimulation(trials);

        System.out.println("Percolation threshold estimate: " + threshold);
    }
}
