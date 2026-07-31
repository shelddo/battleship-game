import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 10;
        int boardHeight = 10;
        Board playerBoard = new Board(BoardType.PLAYER, boardWidth, boardHeight);
        Board oppBoard = new Board(BoardType.ENEMY, boardWidth, boardHeight);
        // desenvolver uma forma que o debugBoard acompanhe o oppBoard, mas mostrando a posição dos navios, para debug.

        playerBoard.placeShip(4, 1);
        playerBoard.placeShip(5, 8);
        playerBoard.placeShip(9,4);

        oppBoard.placeShip(3, 2);
        oppBoard.placeShip(7, 3);
        oppBoard.placeShip(4, 6);

        drawTables(true, playerBoard, oppBoard);

        attack(4, 2, oppBoard);

        drawTables(true, playerBoard, oppBoard);
    }

    private static void attack(int x, int y, Board targetBoard){
        char[][] tgtBoard = targetBoard.getBoard();
        if(tgtBoard[y][x] == 'N'){
            tgtBoard[y][x] = 'X';
            System.out.println("ACERTOU");
        }
        else {
            tgtBoard[y][x] = 'O';
            System.out.println("ERROU");
        }
    }

    private static void drawTables(Board... boards) {
        if (boards.length < 2 || boards.length > 3) {
            throw new IllegalArgumentException("Error: the method requires a minimum of 2 and a maximum of 3 arguments.");
        }

        char[][][] brds = new char[boards.length][][];

        for (int i = 0; i < boards.length; i++) {
            brds[i] = boards[i].getBoard();
        }

        for (char[][] board : brds) {
            // loops through all the rows on the reference table
            for (int row = 0; row < board.length; row++) {
                // prints the index of the columns
                if (row == 0) {
                    for (Board brd : boards) {
                        String boardTypeText;
                        switch (brd.getType()) {
                            case PLAYER:
                                boardTypeText = "Seu tabuleiro:";
                                System.out.print(boardTypeText.toUpperCase());
                                break;
                            case ENEMY:
                                boardTypeText = "Tabuleiro oponente:";
                                System.out.print(boardTypeText.toUpperCase());
                                break;

                            case DEBUG:
                                boardTypeText = "Tabuleiro Debug:";
                                System.out.print(boardTypeText.toUpperCase());
                                break;
                            default:
                                throw new IllegalArgumentException("Error: board type not valid.");
                        }
                        int spaceQnt = (board[row].length * 2 + 2) - boardTypeText.length();
                        if (spaceQnt <= 0) {
                            System.out.print(" " + "| | ");
                        } else {
                            System.out.print(" ".repeat(spaceQnt) + "| | ");
                        }
                    }
                    System.out.println();
                    for (char[][] brd : brds) {
                        System.out.print("  ");
                        for (int col1 = 0; col1 < brd[row].length; col1++) {
                            System.out.print(col1 + " ");
                        }
                        System.out.print("| | ");
                    }
                    System.out.println();
                }
                // prints the table contents of each table side by side
                // todo: make it not print the ships of the enemy board.
                for (int in = 0; in < brds.length; in++) {
                    System.out.print(row + " ");
                    int oppBoardIndex = brds.length + 4;
                    for (int i = 0; i < boards.length; i++) {
                        if (boards[i].getType() == BoardType.ENEMY) {
                            oppBoardIndex = i;
                            break;
                        }
                    }
                    for (int col = 0; col < brds[in][row].length; col++) {
                        if (in == oppBoardIndex) {
                            if (brds[in][row][col] == 'N') {
                                System.out.print('~' + " ");
                            } else {
                                System.out.print(brds[in][row][col] + " ");
                            }
                        } else {
                            System.out.print(brds[in][row][col] + " ");
                        }
                    }
                    System.out.print("| | ");
                }
                System.out.println();
            }
            break;
        }
    }

    private static void drawTables(boolean drawDebug, Board... boards) {
        Board[] brds;
        if (drawDebug) {
            brds = new Board[boards.length + 1];
        } else {
            brds = new Board[boards.length];
        }
        for (int i = 0; i < boards.length; i++) {
            // makes the debug board have the same chars as the opponent board
            if (boards[i].getType() == BoardType.ENEMY && drawDebug) {
                char[][] oppBrd = boards[i].getBoard();

                Board debugBoard = new Board(BoardType.DEBUG, boards[i].getWidth(), boards[i].getHeight());
                for (int row = 0; row < oppBrd.length; row++) {
                    for (int col = 0; col < oppBrd[row].length; col++) {
                        char[][] dbgBoard = debugBoard.getBoard();
                        dbgBoard[row][col] = oppBrd[row][col];
                    }
                }

                Board[] arrBoard = new Board[2];
                arrBoard[0] = boards[i];
                arrBoard[1] = debugBoard;
                for (int j = 0; j < arrBoard.length; j++) {
                    brds[j + 1] = arrBoard[j];
                }
            } else {
                brds[i] = boards[i];
            }
        }
        drawTables(brds);
    }
}
