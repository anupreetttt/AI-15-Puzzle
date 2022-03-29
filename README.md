# Threaded AI 15 Puzzle

## Description: 
In this project I have created a JavaFX program that will allow the user to attempt to solve different 15 puzzles (more on that below). The program have a minimum of 10 unique 15 puzzles to solve. If the user can not solve it and wants to see the solution, animated move by move, they can choose to have the AI puzzle solver figure it out with one of two heuristics.


## Explanation and how to play

To store 10 unique puzzles, we use a map. To get a random puzzle from the map, we use random function to get an index number and get the puzzle at that index number. We store this index number in a list and check it when we again need to get a new puzzle. Once the size of list with index number becomes equal to size of the map, it means we have gone through all puzzles, so we clear the index list. Now the same puzzles will again be shown in a random order when user selects new puzzle.

To show the welcome screen,  we create a task. In this task we show the welcome screen and then make the task sleep for 3 seconds. Once this task is completed, we load the main Ui of the game.

There are functions to shift tiles which is called whenever a button is clicked. We check if the tile is near blank tile or not. If it is then we move it.

There is a menu, which has following options:-
New Puzzle - change the puzzle
AI H1 - use heuristic 1 to solve the puzzle
AI H2 - use heuristic 2 to solve the puzzle
Exit Game - close the application

We override the stop() method of the Application class to shutdown the executor service created in the start method. This stop method is called whenever the application is closed. It is to make sure that executor gets closed every-time, otherwise if any thread is running in background, the application will not be closed properly.

Rest of the methods are self explanatory and there are comments for help which tells you which method does what.
