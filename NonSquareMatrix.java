import java.util.*;

public class NonSquareMatrix extends Matrix implements BasicMatrix {
    public NonSquareMatrix(int row, int col, boolean... bs) {
		super(row, col, bs);
	}
	
	// Solves a system of equations
	public List<String> solveSystem(List<Double> toSolve) {
		if (toSolve.size() != rows) {
			throw new InputMismatchException("Enter a list with size: " + rows);
		}
		
		Matrix clone = cloneMatrix(this);
		for (int i = 0; i < rows; i++) {
			clone.rowMatrix.get(i).add(toSolve.get(i));
		}
		clone = getReducedMatrix(clone, new NonSquareMatrix(1, 1), false);
		
		List<String> solved = new ArrayList<>();
		List<List<Double>> clonedMatrix = clone.rowMatrix;
		int prevPos = clone.columns;
		for (int i = clone.rows - 1; i >= 0; i--) {
			int pos = findFirstVal(clonedMatrix, prevPos, i);
			
			if (pos == prevPos) {
				throw new NoSuchElementException("There is no solution");
			} else if (pos != -1) {
				String res = "";
				for (int j = pos + 1; j < prevPos; j++) {
					solved.add(0, "X" + j + " = " + Double.NaN);
				}
				
				for (int j = pos + 1; j < columns; j++) {
					double val = clonedMatrix.get(i).get(j);
					if (val != 0) {
						res += "X" + j + " + ";
					}
				}

				double val = clonedMatrix.get(i).get(pos);
				double colVal = clonedMatrix.get(i).get(columns);
				res = finalFormat(res, colVal, val, pos);
				solved.add(0, res);
				prevPos = pos;
			}
		}
		return solved;
	}
	
    // Finds the first value in a given row
	private int findFirstVal(List<List<Double>> temp, int prevPos, int row) {
		for (int j = 0; j <= prevPos; j++) {
			Double entry = temp.get(row).get(j);
			if (entry != 0) return j;
		}
		return -1;
	}
	
	// Formats the result to its final version
	private String finalFormat(String res, double colVal, double val, int pos) {
		if (!res.equals("")) {
			res = res.substring(0, res.length() - 2).trim();
			res = "X" + pos + " = (" + colVal + " - (" + res + "))";
		} else {
			res = "X" + pos + " = " + colVal;
		}
		return res;
	}
	
	// Returns the transpose of the given matrix
	protected Matrix transpose(Matrix matrix) {
		Matrix transpose = new NonSquareMatrix(matrix.columns, matrix.rows);
		for (int i = 0; i < matrix.columns; i++) {
			transpose.rowMatrix.add(new ArrayList<>());
		}
		
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				transpose.rowMatrix.get(i).add(matrix.rowMatrix.get(j).get(i));
			}
		}
		return transpose;
	}
	
	// Multiplies current matrix with given matrix
	public Matrix multiply(Matrix other) {
		if (columns != other.rows) {
			throw new IllegalArgumentException("Matrix should have " + columns + " rows");
		}
		Matrix transposedOther = transpose(other);
		Matrix res = new NonSquareMatrix(rows, other.columns);
		for (int i = 0; i < res.rows; i++) {
			List<Double> temp = new ArrayList<>();
			for (int j = 0; j < transposedOther.rows; j++) {
				double sum = 0.0;
				for (int k = 0; k < transposedOther.columns; k++) {
					sum += (rowMatrix.get(i).get(k) * transposedOther.rowMatrix.get(j).get(k));
				}
				temp.add(sum);
			}
			res.rowMatrix.add(temp);
		}
		return res;
	}

	// Checks if the matrix is onto
	public boolean isOnto() {
		if (columns < rows) {
			return false;
		}
		return true;
	}

	// Checks if the matrix is one-to-one
	public boolean isOneToOne() {
		return false;
	}

	// Clones the matrix
	protected Matrix cloneMatrix(Matrix toClone) {
		return null;
	}
}