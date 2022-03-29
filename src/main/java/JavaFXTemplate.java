
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class JavaFXTemplate extends Application {

    final int[] winningArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    ExecutorService executor;
    Map<Integer, int[]> map;
    ArrayList<Integer> shownPuzzles;
    int[] puzzle;
    Button solveButton;
    Label status;
    GridPane board;
    ArrayList<Node> solutionPath;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * this is the overriden method of Application class which is called by
     * calling launch() in main class
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        map = new HashMap<>();
        map.put(0, new int[]{1, 2, 10, 3, 0, 6, 4, 7, 8, 5, 9, 11, 12, 13, 14, 15});
        map.put(7, new int[]{14, 0, 6, 8, 4, 13, 15, 3, 2, 12, 9, 7, 10, 11, 5, 1});
        map.put(9, new int[]{9, 0, 15, 3, 13, 7, 6, 8, 11, 4, 12, 2, 14, 1, 10, 5});
        map.put(8, new int[]{13, 6, 2, 8, 14, 11, 15, 7, 4, 1, 5, 10, 9, 3, 12, 0});
        map.put(1, new int[]{4, 15, 6, 10, 9, 1, 0, 3, 8, 12, 2, 7, 13, 11, 5, 14});
        map.put(2, new int[]{0, 4, 2, 3, 1, 9, 14, 10, 12, 6, 5, 7, 8, 15, 13, 11});
        map.put(5, new int[]{8, 1, 6, 3, 4, 9, 13, 7, 12, 0, 10, 5, 14, 2, 15, 11});
        map.put(3, new int[]{5, 13, 7, 8, 2, 6, 15, 10, 1, 11, 3, 0, 4, 9, 12, 14});
        map.put(4, new int[]{6, 10, 15, 0, 2, 1, 3, 7, 5, 4, 11, 14, 8, 12, 9, 13});
        map.put(6, new int[]{10, 14, 6, 15, 8, 2, 9, 3, 12, 1, 5, 7, 0, 4, 11, 13});

        shownPuzzles = new ArrayList<>();

        executor = Executors.newFixedThreadPool(15);

        Scene scene = new Scene(splashScreen(), 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setTitle("Box 15 Puzzle");

        Task<Void> showSplashScreen = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };

        //after showing the welcome screen for 3 seconds, show the main UI
        showSplashScreen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Scene scene2 = new Scene(createView(), 600, 500);
                primaryStage.setScene(scene2);
                primaryStage.show();
                startGame();
            }
        });

        executor.submit(showSplashScreen);
    }

    /**
     * this method is called when the application is closed. shutdown the
     * executor in this method. otherwise if a thread is running, the program
     * will not be removed from memory
     */
    @Override
    public void stop() {
        //shutdown the executor before closing the program
        executor.shutdown();
    }

    /**
     * Show a splash/welcome screen
     *
     * @return - BorderPane with the splash screen data
     */
    public BorderPane splashScreen() {
        Label label = new Label("Welcome");
        label.setFont(Font.font("System", FontWeight.BOLD, 20));

        BorderPane pane = new BorderPane();
        pane.setCenter(label);
        pane.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        return pane;
    }

    /**
     * used to create the UI
     *
     * @return - GridPane which acts as the root node of our app
     */
    public GridPane createView() {

        //create a GridPane which will be our root node
        GridPane root = new GridPane();

        //Add row and column constraints
        //row constraints
        RowConstraints growingRow = new RowConstraints();
        growingRow.setVgrow(Priority.ALWAYS);
        //column constraints
        ColumnConstraints singleColumn = new ColumnConstraints();
        singleColumn.setPercentWidth(100);
        root.getColumnConstraints().add(singleColumn);
        root.getRowConstraints().addAll(growingRow);

        //create a Menu
        Menu menu = new Menu("Menu");
        MenuItem newPuzzle = new MenuItem("New Puzzle");
        MenuItem algo1 = new MenuItem("AI H1");
        MenuItem algo2 = new MenuItem("AI H2");
        MenuItem exit = new MenuItem("Exit Game");

        //set action handlers for the menu items
        newPuzzle.setOnAction(action -> {
            startGame();
        });
        algo1.setOnAction(action -> {
            solveButton.setDisable(true);
            runHeuristicAlgo("heuristicOne");
        });
        algo2.setOnAction(action -> {
            solveButton.setDisable(true);
            runHeuristicAlgo("heuristicTwo");
        });
        exit.setOnAction(action -> {
            System.exit(0);
        });

        //add the menu items to the menu
        menu.getItems().add(newPuzzle);
        menu.getItems().add(algo1);
        menu.getItems().add(algo2);
        menu.getItems().add(exit);

        //create a menu bar and add the menu to it
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        //add the menu bar to the root
        root.add(menuBar, 0, 0);

        //create the puzzle board
        board = new GridPane();
        board.setPrefSize(350, 350);
        board.setMaxSize(GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE);
        //grid.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        board.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));

        //create and set constraints for board
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(25);
        board.getRowConstraints().addAll(growingRow, growingRow, growingRow, growingRow);
        board.getColumnConstraints().addAll(column, column, column, column);

        //add the board to root
        root.add(board, 0, 1);
        GridPane.setMargin(board, new Insets(45));

        solveButton = new Button("Solve Puzzle For Me(Upto 10 moves)");
        solveButton.setOnAction(action -> {
            solvePuzzle();
        });

        status = new Label();
        status.setFont(Font.font("System", FontWeight.BOLD, 20));
        status.setTextFill(Color.CRIMSON);

        VBox vbox = new VBox(solveButton, status);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);

        GridPane.setHalignment(vbox, HPos.CENTER);
        GridPane.setValignment(vbox, VPos.TOP);
        root.add(vbox, 0, 2);

        return root;
    }

    /**
     * method to start or reset the game. resets all variables to their initial
     * state. Also, gets a random puzzle from the map.
     */
    public void startGame() {

        solveButton.setDisable(true);
        status.setText("");
        if (shownPuzzles.size() == map.size()) {
            shownPuzzles.clear();
        }

        Random rand = new Random();
        int index = 0;
        while (true) {
            index = rand.nextInt(map.size());
            if (!shownPuzzles.contains(index)) {
                break;
            }
        }
        shownPuzzles.add(index);

        createBoard(map.get(index));
    }

    /**
     * method to create the puzzle board
     *
     * @param data - int array which is to be set into the board
     */
    private void createBoard(int[] data) {
        try {
            board.getChildren().clear();
            board.setDisable(false);
            puzzle = data;
            int col = 0;
            int row = 0;
            for (int i = 0; i < data.length; i++) {
                int val = data[i];
                Button button = new Button(data[i] + "");
                button.setId(val + "");
                button.setOnAction(action -> {
                    shiftTile(puzzle, val);
                    createBoard(puzzle);

                    if (hasWon(puzzle)) {
                        status.setText("Congratulations. You solved the puzzle.");
                        board.setDisable(true);
                    }
                });
                button.setPadding(new Insets(10));
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                if (val == 0) {
                    col++;
                } else {
                    board.add(button, col++, row);
                }

                if (i == 3 || i == 7 || i == 11) {
                    row++;
                    col = 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * method to move the shift a valid tile to the empty position or zero
     * position
     *
     * @param arr - int array
     * @param val - value to be moved
     * @return - true or false depending if the tile can be shifted or not
     */
    private boolean shiftTile(int[] arr, int val) {

        int zeroIndex = getIndexInArray(arr, 0);

        //shift tile up
        if (zeroIndex != 0 || zeroIndex != 1 || zeroIndex != 2 || zeroIndex != 3) {
            if (moveUp(arr, zeroIndex, val)) {
                return true;
            }
        }

        //shift tile down
        if (zeroIndex != 12 || zeroIndex != 13 || zeroIndex != 14 || zeroIndex != 15) {
            if (moveDown(arr, zeroIndex, val)) {
                return true;
            }
        }

        //shift tile right
        if (zeroIndex != 0 || zeroIndex != 4 || zeroIndex != 8 || zeroIndex != 12) {
            if (moveRight(arr, zeroIndex, val)) {
                return true;
            }
        }

        //shift tile down
        if (zeroIndex != 3 || zeroIndex != 7 || zeroIndex != 11 || zeroIndex != 15) {
            if (moveLeft(arr, zeroIndex, val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method used to get index of value 0 in an int array
     *
     * @param arr - int array
     * @param val - value which position we have to get in the array
     * @return the index of the value in the array. if value is not present,
     * return -1
     */
    public int getIndexInArray(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * move the tile up if possible
     *
     * @param arr - int array
     * @param zeroIndex - position of 0 in the array
     * @param val - the value which needs to be swapped
     * @return - true if it can be swapped, false otherwise
     */
    public boolean moveUp(int[] arr, int zeroIndex, int val) {
        if (zeroIndex + 4 > 15 || arr[zeroIndex + 4] != val) {
            return false;
        }
        moveZero(zeroIndex, val);
        return true;
    }

    /**
     * move the tile down if possible
     *
     * @param arr - int array
     * @param zeroIndex - position of 0 in the array
     * @param val - the value which needs to be swapped
     * @return - true if it can be swapped, false otherwise
     */
    public boolean moveDown(int[] arr, int zeroIndex, int val) {
        if (zeroIndex - 4 < 0 || arr[zeroIndex - 4] != val) {
            return false;
        }
        moveZero(zeroIndex, val);
        return true;
    }

    /**
     * move the tile right if possible
     *
     * @param arr - int array
     * @param zeroIndex - position of 0 in the array
     * @param val - the value which needs to be swapped
     * @return - true if it can be swapped, false otherwise
     */
    public boolean moveRight(int[] arr, int zeroIndex, int val) {
        if (zeroIndex - 1 < 0 || arr[zeroIndex - 1] != val) {
            return false;
        }
        moveZero(zeroIndex, val);
        return true;
    }

    /**
     * move the tile left if possible
     *
     * @param arr - int array
     * @param zeroIndex - position of 0 in the array
     * @param val - the value which needs to be swapped
     * @return - true if it can be swapped, false otherwise
     */
    public boolean moveLeft(int[] arr, int zeroIndex, int val) {
        if (zeroIndex + 1 > 15 || arr[zeroIndex + 1] != val) {
            return false;
        }
        moveZero(zeroIndex, val);
        return true;
    }

    /**
     * moves the zero to another spot (swaps values)
     *
     * @param puzzle2
     * @param zeroIndex
     * @param moveToIndex
     */
    public void moveZero(int zeroIndex, int val) {
        int moveToIndex = getIndexInArray(puzzle, val);
        int temp = puzzle[moveToIndex];
        puzzle[zeroIndex] = temp;
        puzzle[moveToIndex] = 0;
    }

    /**
     * method to get solution for current puzzle iteration
     *
     * @param heuristicName - which heuristic to run - one or two
     */
    public void runHeuristicAlgo(String heuristicName) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                solveButton.setDisable(true);
                Node startState = new Node(puzzle);
                DB_Solver2 start_A_Star = new DB_Solver2(startState, heuristicName);
                Node solution = start_A_Star.findSolutionPath();
                solutionPath = start_A_Star.getSolutionPath(solution);
                solutionPath.forEach(e -> {
                    for (int i : e.getKey()) {
                        System.out.print(i + " ");
                    }
                    System.out.println("");
                });
                solveButton.setDisable(false);
            }
        });
    }

    /**
     * solve the puzzle up to 10 moves ahead from the current iteration
     */
    public void solvePuzzle() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                solveButton.setDisable(true);
                for (int i = 0; i < 10 && i < solutionPath.size(); i++) {
                    int[] currentArr = solutionPath.get(i).getKey();

                    Platform.runLater(() -> {
                        createBoard(currentArr);
                        puzzle = currentArr;
                        if (hasWon(currentArr)) {
                            createBoard(currentArr);
                            status.setText("The puzzle has been solved.");
                            board.setDisable(true);
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(JavaFXTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * method to test if the game is won or not
     *
     * @param arr - int array
     * @return - true if game is over and false if it is not
     */
    public boolean hasWon(int[] arr) {
        if (Arrays.equals(winningArr, arr)) {
            return true;
        } else {
            return false;
        }
    }
}
