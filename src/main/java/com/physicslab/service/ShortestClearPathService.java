package com.physicslab.service;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Service;

@Service
public class ShortestClearPathService {

  private static final int[][] DIRECTIONS = {
      { -1, -1 }, { -1, 0 }, { -1, 1 },
      { 0, -1 }, { 0, 1 },
      { 1, -1 }, { 1, 0 }, { 1, 1 }
  };

  public ShortestClearPathResponse shortestPath(int[][] grid) {
    validate(grid);

    int rows = grid.length;
    int columns = grid[0].length;

    if (grid[0][0] == 1) {
      return new ShortestClearPathResponse(-1);
    }

    boolean[][] seen = new boolean[rows][columns];
    Queue<State> queue = new LinkedList<>();
    queue.add(new State(0, 0, 1));
    seen[0][0] = true;

    while (!queue.isEmpty()) {
      State state = queue.remove();
      int row = state.row;
      int col = state.col;
      int steps = state.steps;

      if (row == rows - 1 && col == columns - 1) {
        return new ShortestClearPathResponse(steps);
      }

      for (int[] direction : DIRECTIONS) {
        int nextRow = row + direction[0];
        int nextCol = col + direction[1];

        if (!isValid(nextRow, nextCol, rows, columns, grid) || seen[nextRow][nextCol]) {
          continue;
        }

        seen[nextRow][nextCol] = true;
        queue.add(new State(nextRow, nextCol, steps + 1));
      }
    }

    return new ShortestClearPathResponse(-1);
  }

  private static boolean isValid(int row, int col, int rows, int columns, int[][] grid) {
    return row >= 0 && row < rows && col >= 0 && col < columns && grid[row][col] == 0;
  }

  private static void validate(int[][] grid) {
    if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
      throw new IllegalArgumentException("matrix must be a non-empty square matrix");
    }

    int rows = grid.length;
    int columns = grid[0].length;

    if (rows != columns) {
      throw new IllegalArgumentException("matrix must be an n x n square matrix");
    }

    for (int row = 0; row < rows; row++) {
      if (grid[row] == null || grid[row].length != columns) {
        throw new IllegalArgumentException("matrix rows must all be the same length");
      }

      for (int col = 0; col < columns; col++) {
        if (grid[row][col] != 0 && grid[row][col] != 1) {
          throw new IllegalArgumentException("matrix values must be either 0 or 1");
        }
      }
    }
  }

  private record State(int row, int col, int steps) {
  }
}
