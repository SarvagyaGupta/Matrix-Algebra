import java.util.*;

abstract class Matrix implements BasicMatrix {
	public int rows;
    public int columns;
    public List<List<Double>> rowMatrix;

	// Constructs a matrix with the given row or column
    // If a boolean value is given, the user is prompted to fill the array
    // Throws IllegalArgumentException if (row <= 0 || col <= 0)
	public Matrix(int row, int col, boolean ...bs) {
		if (row <= 0 || col <= 0) throw new IllegalArgumentException();
		rows = row;
		columns = col;
        rowMatrix = new ArrayList<>();
		if (bs != null) makeMatrix();
	}
	
	// Prompts the user to fill the new Matrix
	private void makeMatrix() {
        Scanner console = new Scanner(System.in);
        for (int i = 0; i < rows; i++) {
            List<Double> temp = new ArrayList<>();
            System.out.println("Currently filling row " + (i + 1));
            for (int j = 0; j < columns; j++) {
                System.out.print("Entry " + (j + 1) + ": ");
                temp.add(console.nextDouble());
            } 
            rowMatrix.add(temp);
        }
        console.close();
    }
	
	// Returns the passed value rounded to 2 digits
	public double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
    
    // Clones and returns the given matrix
    protected abstract Matrix cloneMatrix(Matrix toClone);
	
    // Takes in a matrix and returns the sum of the itself and the other matrix
    // Throws IllegalArgumentException if (other.columns != columns || other.rows != rows)
	public Matrix addMatrix(Matrix other) {
		if (other.columns != columns || other.rows != rows) {
			throw new IllegalArgumentException("Enter a " + rows + 
					" * " + columns + "matrix");
		}
		Matrix result = cloneMatrix(this);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double add = rowMatrix.get(i).get(j) + other.rowMatrix.get(i).get(j);
				result.rowMatrix.get(i).set(j, add);
			}
		}
		return result;
	}
    
	// Prints the matrix
    public void printMatrix() {
        printAnyMatrix(this);
    }
    
    // Prints the matrix passed in as a parameter
    private void printAnyMatrix(Matrix matrix) {
        for (List<Double> list: matrix.rowMatrix) {
            for (Double entry: list) {
            	double curr = round(entry);
            	if (entry >= 0) System.out.print("  " + curr);
            	else System.out.print(" " + curr);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    // Returns the homogeneous solution of a matrix
    public List<String> solveHomogeneous() {
    	List<Double> toSolve = new ArrayList<>();
    	for (int i = 0; i < rows; i++) {
    		toSolve.add(0.0);
    	}
    	return solveSystem(toSolve);
    }
    
    // Checks if the matrix has linearly independent columns
    public boolean isLinearlyIndependent() {
    	return isOneToOne();
    }
	
    // Returns the transpose of the matrix
	public Matrix transpose() {
		return transpose(this);
	}
	
	protected abstract Matrix transpose(Matrix matrix);
	
	// Returns the column span
	public List<List<Double>> getColSpan() {
		Matrix res = transpose();
		return res.rowMatrix;
	}
	
	// Returns the row span
	public List<List<Double>> getRowSpan() {
		return rowMatrix;
	}
	
	// Returns the reduced form of the matrix
    public Matrix getReducedMatrix() {
    	return getReducedMatrix(this, new NonSquareMatrix(1, 1), false);
    }

	// Returns the RREF of a matrix 
    protected Matrix getReducedMatrix(Matrix toReduce, Matrix other, boolean check) {
    	Matrix matrix = cloneMatrix(toReduce);
        simpleReduce(matrix, other, check);
    	completeReduce(matrix, other, check);
    	makePivots(matrix, other, check);
        return matrix;
    }
    
    // Gets the matrix into reduced echelon form
    private void simpleReduce(Matrix matrix, Matrix other, boolean check) {
    	for (int i = 0, pos = 0; i < matrix.rows - 1 && pos < matrix.columns; i++, pos++) {
            if (getNonZeroToPos(matrix.rowMatrix, other.rowMatrix, i, pos, check)) {
                for (int j = i + 1; j < matrix.rows; j++) {
                	if (pos != -1) {
                		double val = reduceTwoRows(matrix.rowMatrix, i, j, pos);
                    	if (check) reduceByVal(other.rowMatrix, i, j, val);
                	}
                }	
            } else i--;
        }
    }
    
    // Makes all pivot columns into 0 except for the pivot
    private void completeReduce(Matrix matrix, Matrix other, boolean check) {
    	for (int i = matrix.rows - 1; i > 0; i--) {
        	int pos = findNextPos(matrix.rowMatrix, i);
        	if (pos != -1) {
        		for (int j = i - 1; j >= 0; j--) {
            		double val = reduceTwoRows(matrix.rowMatrix, i, j, pos);
            		if (check) reduceByVal(other.rowMatrix, i, j, val);
            	}
        	}
        }
    }
    
    // Makes all pivots to 1
    private void makePivots(Matrix matrix, Matrix other, boolean check) {
    	for (int i = 0; i < matrix.rows; i++) {
			int pos = findNextPos(matrix.rowMatrix, i);
			if (pos == -1) return;
			List<Double> row = matrix.rowMatrix.get(i);
			double val = row.get(pos);
			if (val != 0) {
				for (int j = 0; j < matrix.columns; j++) {
					if (row.get(j) != 0) row.set(j, row.get(j) / val);
				}
			}
			if (check) {
				row = other.rowMatrix.get(i);
				for (int j = 0; j < other.columns; j++) {
					if (row.get(j) != 0) row.set(j, row.get(j) / val);
				}
			}
		}
    }
    
    // Takes in a matrix, the first row idex, the second row index, and the current pos
    // and reduces the second row in the matrix
    protected double reduceTwoRows(List<List<Double>> matrix, int firstRow, int secondRow, int pos) {
        List<Double> rowOne = matrix.get(firstRow);
        List<Double> rowTwo = matrix.get(secondRow);
        if (rowOne.get(pos) == 0) return Double.NaN;
        double reduceBy = rowTwo.get(pos) / rowOne.get(pos);
        for (int i = 0; i < rowTwo.size(); i++) {
        	double value = rowTwo.get(i) - reduceBy * rowOne.get(i);
            rowTwo.set(i, value);
        }
        return reduceBy;
    }
    
    // Takes in a matrix, the first row idex, the second row index, and 
    // reduces the second row in the matrix by the given value
    private void reduceByVal(List<List<Double>> matrix, int firstRow, int secondRow, double val) {
    	if (val != Double.NaN) {
        	List<Double> rowOne = matrix.get(firstRow);
            List<Double> rowTwo = matrix.get(secondRow);
            for (int i = 0; i < rowTwo.size(); i++) {
            	double value = rowTwo.get(i) - val * rowOne.get(i);
                rowTwo.set(i, value);
            }
    	}
    }
    
    // Gets a row that has a non-zero entry at pos to row. Returns true if done successfully
    protected boolean getNonZeroToPos(List<List<Double>> matrix, List<List<Double>> other, int row, 
    		int pos, boolean check) {
        List<Double> posRow = matrix.get(row);
        double value = posRow.get(pos);
        if (value != 0) return true;
        
        for (int i = pos + 1; i < matrix.size(); i++) {
            List<Double> temp = matrix.get(i);
            if (temp.get(pos) != 0) {
                swapRows(matrix, pos, i);
                if (check) swapRows(other, pos, i);
                return true;
            }
        }
        return false;
    }
    
    // Swaps to rows
    private void swapRows(List<List<Double>> matrix, int firstRow, int secondRow) {
        List<Double> temp = matrix.get(firstRow);
        matrix.set(firstRow, matrix.get(secondRow));
        matrix.set(secondRow, temp);
    }

    // Finds the next pivot position
	private int findNextPos(List<List<Double>> matrix, int index) {
    	List<Double> temp = matrix.get(index);
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) != 0) return i;
		}
    	return -1;
    }
}
