public class Board {
    /**
     * The type variable is used to inform if the Board object is either the player's board, opponent board or even
     * Debug board.
     * Receives "PLAYER", "ENEMY" or "DEBUG".
     */
    BoardType type;
    private final int width;
    private final int height;
    private final char[][] board;

    public Board(BoardType type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.board = new char[height][width];
        buildBoard(width, height);
    }

    private void buildBoard(int width, int height) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = '~';
            }
        }
    }

    public boolean placeShip(int x, int y) {
        // x -> cols / y -> rows
        if (board[y][x] != 'N') {
            board[y][x] = 'N';
            return true;
        }
        else {
            System.out.println("Coordenadas já possuem um navio.");
            return false;
        }
    }

    public boolean removeShip(int x, int y) {
        // x -> cols / y -> rows
        if (board[y][x] == 'N') {
            board[y][x] = '~';
            return true;
        }
        else {
            System.out.println("Não há navio nas coordenadas.");
            return false;
        }
    }

    public int getShipsAlive() {
        int shipsAlive = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'N') {
                    shipsAlive++;
                }
            }
        }
        return shipsAlive;
    }

    public char[][] getBoard() {
        return board;
    }

    public BoardType getType() {
        return type;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
