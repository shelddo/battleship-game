import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public enum Scene {
        MENU, PLACE_SHIPS, PRE_GAME, PLAYER_TURN, PC_TURN, GAME_OVER, WIN
    }

    public static void main(String[] args) {
        int boardWidth = 10;
        int boardHeight = 10;
        Board playerBoard = new Board(BoardType.PLAYER, boardWidth, boardHeight);
        Board oppBoard = new Board(BoardType.ENEMY, boardWidth, boardHeight);

        int opcao = 0;
        while (opcao != 2) {
            Scanner input = new Scanner(System.in);
            drawMenu();
            opcao = input.nextInt();
            switch (opcao) {
                case 1:
                    game(playerBoard, oppBoard);
                    break;
                case 2:
                    break;
            }
        }
    }

    private static void game(Board playerBoard, Board oppBoard) {
        int option = 0;
        while (option != 2) {
            Scene turn = Scene.PRE_GAME;
            Scanner input = new Scanner(System.in);
            System.out.print("\n".repeat(40));
            // player places their ships
            placeShips(playerBoard, oppBoard, input);

            while (turn == Scene.PRE_GAME) {
                System.out.print("\n".repeat(40));
                // coin toss before starting the game
                System.out.println("            Escolha:            ");
                System.out.println("1 - Cara               2 - Coroa");
                int coinToss = input.nextInt();
                if (coinToss < 1 || coinToss > 2) {
                    System.out.println("Por favor, escolha uma opção válida.");
                } else {
                    System.out.println("Sorteando...");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Random random = new Random();
                    if (coinToss == random.nextInt(2) + 1) {
                        turn = Scene.PLAYER_TURN;
                        System.out.println("Você começa!");
                    } else {
                        turn = Scene.PC_TURN;
                        System.out.println("O computador começa.");
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.print("\n".repeat(40));
            System.out.println("Jogo começando...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // actual game
            game:
            while (true) {
                if (turn == Scene.PLAYER_TURN || turn == Scene.PC_TURN) {
                    System.out.print("\n".repeat(40));
                    drawBoards(playerBoard, oppBoard);
                }
                if (turn == Scene.PLAYER_TURN) {
                    System.out.println("O que você quer fazer?");
                    System.out.println("1 - Atacar");
                    System.out.println("2 - Desistir");
                    int opt;
                    try {
                        opt = input.nextInt();
                    } catch (InputMismatchException e) {
                        opt = 0;
                    }
                    input.nextLine();
                    label:
                    switch (opt) {
                        case 1:
                            System.out.println("Informe as coordenadas para atacar (x, y):");
                            String coords = input.nextLine();
                            String[] coord = coords.split(",");
                            if (coord.length >= 2) {
                                String x = coord[0].trim();
                                String y = coord[1].trim();

                                if (Integer.parseInt(x) > oppBoard.getWidth() || Integer.parseInt(y) > oppBoard.getHeight()
                                        || Integer.parseInt(x) < 0 || Integer.parseInt(y) < 0) {
                                    System.out.println("As coordenadas devem estar dentro do tamanho do tabuleiro.");
                                    try {
                                        TimeUnit.SECONDS.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }

                                try {
                                    String result = attack(Integer.parseInt(x), Integer.parseInt(y), oppBoard);
                                    switch (result) {
                                        case "INVALID":
                                            System.out.println("A coordenada informada já foi atacada. Tente novamente.");
                                            try {
                                                TimeUnit.SECONDS.sleep(1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break label;
                                        case "MISS":
                                            System.out.println("Você errou.");
                                            break;
                                        case "HIT":
                                            System.out.println("Você acertou!");
                                            break;
                                    }
                                    if (oppBoard.getShipsAlive() > 0) {
                                        turn = Scene.PC_TURN;
                                    } else {
                                        System.out.println("Você ganhou!!!");
                                        turn = Scene.WIN;
                                    }
                                    try {
                                        TimeUnit.SECONDS.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Por favor, forneça um número válido.");
                                }
                            }
                            break;
                        case 2:
                            turn = Scene.GAME_OVER;
                            break;
                        default:
                            System.out.println("Opção inválida!");
                    }
                } else if (turn == Scene.PC_TURN) {
                    System.out.println("O computador está decidindo sua ação...");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Random random = new Random();
                    String result;
                    int xAttack;
                    int yAttack;
                    do {
                        xAttack = random.nextInt(playerBoard.getWidth());
                        yAttack = random.nextInt(playerBoard.getHeight());
                        result = attack(xAttack, yAttack, playerBoard);
                    } while (result.equals("INVALID"));
                    switch (result) {
                        case "MISS":
                            System.out.println("O computador atacou as coordenadas " + xAttack + ", " + yAttack + " e errou.");
                            break;
                        case "HIT":
                            System.out.println("O computador atacou as coordenadas " + xAttack + ", " + yAttack + " e acertou.");
                            break;
                        default:
                            System.out.println("?????"); // not possible to happen
                            break;
                    }
                    if (playerBoard.getShipsAlive() > 0) {
                        turn = Scene.PLAYER_TURN;
                    } else {
                        turn = Scene.GAME_OVER;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (turn == Scene.GAME_OVER) {
                    drawGameOverScreen();
                    int opt = input.nextInt();
                    switch (opt) {
                        case 1:
                            break game;
                        case 2:
                            option = 2;
                            break game;
                    }
                } else if (turn == Scene.WIN) {
                    drawWinScreen();
                    int opt = input.nextInt();
                    switch (opt) {
                        case 1:
                            break game;
                        case 2:
                            option = 2;
                            break game;
                    }
                }
            }
        }
    }

    private static void drawWinScreen() {
        System.out.println("========================");
        System.out.println("==    Você ganhou!    ==");
        System.out.println("========================");
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Jogar de novo");
        System.out.println("2 - Sair para o menu principal.");
    }

    private static void drawGameOverScreen() {
        System.out.println("========================");
        System.out.println("==    Você perdeu.    ==");
        System.out.println("========================");
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Jogar de novo");
        System.out.println("2 - Sair para o menu principal.");
    }

    private static void placeShips(Board playerBoard, Board oppBoard, Scanner input) {
        int shipsRemaining = 3;
        for (int i = 0; i < shipsRemaining; i++) {
            computerPlaceShips(oppBoard.getWidth(), oppBoard.getHeight(), oppBoard);
        }
        while (shipsRemaining > 0) {
            System.out.println("Escolha onde posicionar seus navios:");
            drawBoard(playerBoard);
            System.out.println("\n Barcos restantes a serem colocados: " + shipsRemaining);
            System.out.println("Digite as coordenadas, separadas por virgula (x, y). Sendo 'x' o número da coluna e 'y' o número da linha:");
            String coords = input.nextLine();
            String[] coord = coords.split(",");
            if (coord.length >= 2) {
                String x = coord[0].trim();
                String y = coord[1].trim();

                try {
                    playerBoard.placeShip(Integer.parseInt(x), Integer.parseInt(y));
                    shipsRemaining--;
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, forneça um número válido.");
                }
            }
        }
    }

    private static void drawBoard(Board board) {
        char[][] aboard = board.getBoard();
        for (int row = 0; row < aboard.length; row++) {
            if (row == 0) {
                System.out.print("  ");
                for (int col = 0; col < aboard[row].length; col++) {
                    System.out.print(col + " ");
                }
                System.out.println();
            }
            System.out.print(row + " ");
            for (int col = 0; col < aboard[row].length; col++) {
                System.out.print(aboard[row][col] + " ");
            }
            System.out.println();
        }
    }

    private static void drawMenu() {
        System.out.println("=======================");
        System.out.println("==   BATALHA NAVAL   ==");
        System.out.println("=======================");
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Jogar");
        System.out.println("2 - Sair (só tem essas por enquanto).");
    }

    private static void computerPlaceShips(int boardWidth, int boardHeight, Board board) {
        Random random = new Random();
        boolean placed;
        do {
            placed = board.placeShip(random.nextInt(boardWidth), random.nextInt(boardHeight));
        } while (!placed);
    }

    private static String attack(int x, int y, Board targetBoard) {
        char[][] tgtBoard = targetBoard.getBoard();
        if (tgtBoard[y][x] == 'N') {
            tgtBoard[y][x] = 'X';
            return "HIT";
        } else if (tgtBoard[y][x] == '~') {
            tgtBoard[y][x] = 'O';
            return "MISS";
        } else {
            return "INVALID";
        }
    }

    private static void drawBoards(Board... boards) {
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

    private static void drawBoards(boolean drawDebug, Board... boards) {
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
        drawBoards(brds);
    }
}
