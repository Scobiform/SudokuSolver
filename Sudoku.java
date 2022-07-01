import java.io.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sudoku
{
	/**
	 * Sudoku solver that solves all given Sudokus from a textfile with a backtracking algorithm
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception
    {
		try
		{
			// Instance of Sudoku
			Sudoku sudoku = new Sudoku();
			
			// Instance of SudokuReader
			SudokuReader sudokuReader = sudoku.new SudokuReader();

			// Instance of SudokuSolver
			SudokuSolver sudokuSolver = sudoku.new SudokuSolver();

			// Sudoku puzzle´s by ...
			String fileName = "17_Hints.sudoku";
			//String fileName = "hardSudoku.sudoku";
			var linkedList = sudokuReader.readSudokusFromFile(fileName);
			// Sudoku counter
			int counter = 0;
			// Initiate timer
			Instant start = Instant.now();
			// Iterate through the Sudoku linkedList
			for(int[][] element : linkedList)
			{
				System.out.println("\n" + counter + "\n");
				System.out.print("Sudoku to solve: \n");
				sudoku.printGrid(element);
				System.out.print("\n");
				System.out.print("Solved Sudoku: \n");
				sudokuSolver.solve(element);
				sudoku.printGrid(element);
				//System.out.print("Solved Str:" + sudoku.gridToString(element) + "\n");
				// linkedList.size()
				counter++;
				if(counter == 100)
				{ 
					break;
				}
			}
			Instant end = Instant.now();
			System.out.println("\n" + Duration.between(start, end));
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
    }
	/**
	 * Converts a string to a sudoku grid
	 * @param line
	 * @return grid
	 */
	private int[][] getGridFromString(String line)
	{	
		// Set empty sudoku matrix
		int[][] grid = new int[9][9];

		// Parameter sudoku string
		String strCode = line;

		int gridCellSize = grid.length * grid[0].length;
		int cellCounter = 0;

		while(cellCounter < gridCellSize)
		{
			for(int row = 0; row < grid.length; row++)
			{
				for(int col = 0; col < grid[row].length; col++)
				{
					grid[row][col] = Character.getNumericValue(strCode.charAt(cellCounter));
					cellCounter++;
				}
			}
		}			
		return grid;
	}

	/**
	 * Converts a grid to a String
	 * @param grid
	 * @return sudokuString
	 */
	private String gridToString(int[][] grid)
	{
		int row = grid.length;
		int col = grid[0].length;

		String sudokuString = "";

		for(int i = 0; i < row; i++)
		{
			for(int h = 0; h < col; h++)
			{
				sudokuString += grid[i][h];
			}
		}
		return sudokuString;
	}

	/**
	 * Prints a grid to the console
	 * @param grid
	 */
	private void printGrid(int[][] grid)
	{
		// Row of the sudoku grid
		int row = grid.length;
		// Column of the sudoku grid
		int col = grid[0].length;

		for(int i = 0; i < row; i++)
		{
			if(i % 3 == 0)
			{
				for(int fillUC = 0; fillUC < 31; fillUC++)
				{
					System.out.print("+");
				}
				System.out.print("\n");
			}
			for(int h = 0; h < col; h++)
			{
				if(h % 3 == 0)
				{
					System.out.print("|");
				}		
				System.out.print(" " + grid[i][h] + " ");	
			}
			System.out.print("|\n");
		}
		for(int fillUC = 0; fillUC < 31; fillUC++)
		{
			System.out.print("+");
		}
		System.out.print("\n");
	}

	/**
	 * Checks if a sudoku grid is a valid 9x9
	 * Number only once in a row
	 * Number only once in a column
     * Number only once in a 3x3 matrix block
	 * Throws IllegalArgumentException
	 * @param grid
	 * @param row
	 * @param col
	 * @param num
	 * @return true || false
	 */
	private boolean isValid(int[][] grid, int row, int col, int num) throws IllegalArgumentException
	{
		return (checkRow(grid, row, num) && checkColumn(grid, col, num) && check3x3Matrix(grid, row, col, num));
	}

	/**
	 * Check if a number is only once in a column of a 9x9 matrix grid
	 * @param grid
	 * @param col
	 * @param num
	 * @return true || false
	 */
	private boolean checkColumn(int[][] grid, int col, int num)
	{		
		for (int i = 0; i < grid.length; i++)
		{
			if (grid[i][col] == num)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if a number is only once in a row of a 9x9 matrix grid
	 * @param grid
	 * @param row
	 * @param num
	 * @return true || false
	 */
	private boolean checkRow(int[][] grid, int row, int num)
	{
		for (int i = 0; i < grid[row].length; i++)
		{		
			if (grid[row][i] == num)
			{
				return false;
			}		
		}
		return true;
	}

	/**
	 * Check if 3x3 grid in a 9x9 Matrix is valid (Number only once)
	 * @param grid
	 * @param row
	 * @param col
	 * @param num
	 * @return true || false
	 */
	private boolean check3x3Matrix(int[][] grid, int row, int col, int num)
	{
		for(int i = (row / 3) * 3; i < ((row / 3) * 3) + 3; i++)
		{
			for(int j = (col / 3) * 3; j < ((col / 3) * 3) + 3; j++) 
			{
				if(grid[i][j] == num) 
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Reads sudokus line by line from a file, converts them to a valid int[][] sudoku grid and adds them to a LinkedList
	 */
	public class SudokuReader
	{
		/**
		 * Reads sudokus from a file and adds them line by line to a LinkedList with 2D int arrays (grid)
		 * @param filename
		 * @return LinkedList<int[][]> result
		 * @throws ParseException
		 */
		private LinkedList<int[][]> readSudokusFromFile(String filename) throws ParseException
		{
			// Initiate file
			File file = new File(filename);
			// Check if file exists and is file
			if(!file.exists() || !file.isFile())
			{
				throw new IllegalArgumentException("File: " + filename + " not found!");
			}

			// Set BufferedReader to null
			BufferedReader reader = null;
			// Create new LinkedList
			LinkedList<int[][]> result = new LinkedList<int[][]>();
		
			try
			{
				// Try reading file line by line
				reader = new BufferedReader(new FileReader(file));
				String line;
				while((line = reader.readLine()) != null)
				{
					// Convert string line to a int[][] grid
					int[][] tempGrid = getGridFromString(line);
					// Add grid to the LinkedList
					result.add(tempGrid);
				}
			}
			catch(IOException ex)
			{
				System.out.print(ex);
			}
			// LinkedList output
			return result;
		}
	}

	/**
	 * Solves Sudokus with Backtracking
	 * -> Solve the first 10 Sudokus under 100 seconds (currently 9 seconds)
	 */
	public class SudokuSolver
	{
		/**
		 * Overwrite the sudoku grid with the solved numbers
		 * @param grid
		 */
		private boolean solve(int[][] grid)
		{
			// Get coords of empty values
			int[] emptyValue = empty(grid); 

			// if coords (-1, -1)
			if (emptyValue[0] == -1) {
				// return true when all empty values are filled
				return true;
			}
			
			// Set coords
			int row = emptyValue[0];
			int col = emptyValue[1];
    
			// Loop through the numbers 1 - 9
			for (int num = 1; num <= 9; num++) 
			{
				// Check if grid is valid
				if (isValid(grid, row, col, num)) 
				{
					// If valid, set the correct number
					grid[row][col] = num;
					// Recursion
					boolean check = solve(grid);
					if (check == true) {
						return true;
					}
					// Reset if conditions fail
					grid[row][col] = 0; 
				}
			}
			// Recursion trigger
			return false; 
		}
		
		/**
		 * Check for empty (0) grid positions, return (-1, -1) if everything is filled
		 * @param grid
		 * @return emptyValue
		 */
		private int[] empty(int[][] grid) 
		{ 
			// Coords array for empty values
			int[] emptyValue = new int[2];
			// Set to -1
			emptyValue[0] = -1;
			emptyValue[1] = -1;
	 
			// Check for empty
			for (int row = 0; row < grid.length; row++) 
			{
				for (int col = 0; col < grid.length; col++) 
				{
					if (grid[row][col] == 0) 
					{
						// get cords
						emptyValue[0] = row;
						emptyValue[1] = col;
						return emptyValue;
					}
				}
			}
			// (-1, -1) if everything is filled
			return emptyValue;
		}
	}
}