package com.physicslab.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RowEchelonService {

  private static final double RELATIVE_TOLERANCE = 1.0e-10;

  public RowEchelonResponse reduce(Double[][] input) {
    validate(input);

    int rowCount = input.length;
    int columnCount = input[0].length;
    double[][] matrix = copy(input);
    double scale = 0.0;
    for (double[] row : matrix) {
      for (double value : row) {
        scale = Math.max(scale, Math.abs(value));
      }
    }
    double tolerance = scale * RELATIVE_TOLERANCE;

    List<Integer> pivotColumns = new ArrayList<>();
    int pivotRow = 0;

    for (int column = 0; column < columnCount && pivotRow < rowCount; column++) {
      int bestRow = pivotRow;
      for (int row = pivotRow + 1; row < rowCount; row++) {
        if (Math.abs(matrix[row][column]) > Math.abs(matrix[bestRow][column])) {
          bestRow = row;
        }
      }

      if (Math.abs(matrix[bestRow][column]) <= tolerance) {
        for (int row = pivotRow; row < rowCount; row++) {
          matrix[row][column] = 0.0;
        }
        continue;
      }

      swapRows(matrix, pivotRow, bestRow);
      double pivot = matrix[pivotRow][column];

      for (int row = pivotRow + 1; row < rowCount; row++) {
        double factor = matrix[row][column] / pivot;
        matrix[row][column] = 0.0;
        for (int nextColumn = column + 1; nextColumn < columnCount; nextColumn++) {
          double reducedValue = matrix[row][nextColumn] - factor * matrix[pivotRow][nextColumn];
          if (!Double.isFinite(reducedValue)) {
            throw new IllegalArgumentException("matrix values overflow during elimination");
          }
          if (Math.abs(reducedValue) <= tolerance) {
            matrix[row][nextColumn] = 0.0;
          } else {
            matrix[row][nextColumn] = reducedValue;
          }
        }
      }

      pivotColumns.add(column);
      pivotRow++;
    }

    return new RowEchelonResponse(matrix, pivotColumns.size(), List.copyOf(pivotColumns));
  }

  private static void validate(Double[][] matrix) {
    if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
      throw new IllegalArgumentException("matrix must have at least one row and one column");
    }

    int columnCount = matrix[0].length;
    for (int row = 0; row < matrix.length; row++) {
      if (matrix[row] == null || matrix[row].length != columnCount) {
        throw new IllegalArgumentException("matrix must be rectangular and cannot contain null rows");
      }
      for (Double value : matrix[row]) {
        if (value == null || !Double.isFinite(value)) {
          throw new IllegalArgumentException("matrix entries must be finite numbers");
        }
      }
    }
  }

  private static double[][] copy(Double[][] matrix) {
    double[][] result = new double[matrix.length][];
    for (int row = 0; row < matrix.length; row++) {
      result[row] = new double[matrix[row].length];
      for (int column = 0; column < matrix[row].length; column++) {
        result[row][column] = matrix[row][column];
      }
    }
    return result;
  }

  private static void swapRows(double[][] matrix, int first, int second) {
    if (first != second) {
      double[] row = matrix[first];
      matrix[first] = matrix[second];
      matrix[second] = row;
    }
  }
}
