import java.util.*;

public class SquareMatrix extends Matrix implements BasicMatrix {
	public SquareMatrix() {
		super();
	}
	
	public SquareMatrix(int size) {
		super(size, size);
	}
	
	// Returns the size of the square matrix
	public int getSize() {
		return rows;
	}
	
	// Returns the homogeneous solution of a matrix
    public List<Double> solveHomogeneous() {
    	List<Double> toSolve = new ArrayList<>();
    	for (int i = 0; i < rows; i++) {
    		toSolve.add(0.0);
    	}
    	return solveSystem(toSolve);
    }

	// Solves the given system of equations
	public List<Double> solveSystem(List<Double> toSolve) {
		if (toSolve.size() != rows) 
			throw new InputMismatchException("Enter a list with size: " + rows);
		
		List<Double> res = new ArrayList<>();
		double det = determinant();
		if (det == 0)
			throw new IllegalStateException("Determinant is zero. Use NonSquareMatrix");
		
		Matrix transpose = transpose();
		List<List<Double>> matrix = transpose.rowMatrix;
		for (int i = 0; i < rows; i++) {
			List<Double> row = matrix.remove(i);
			matrix.add(i, toSolve);
			res.add(determinant(transpose(transpose)) / det);
			matrix.remove(i);
			matrix.add(i, row);
		}
		return res;
	}
	
	// Finds the determinant of the matrix
	public double determinant() {
		return determinant(this);
	}
	
	// Returns the determinant of the given matrix using row reduction
	private double determinant(Matrix toFind) {
		if (rows != columns) {
			throw new IllegalStateException("Matrix is not square. There is no determinant");
		}
		
		Matrix matrix = cloneMatrix(toFind);
    	int pos = 0;
    	int sign = 1;
        for (int i = 0; i < matrix.rows - 1 && pos < matrix.columns; i++) {
        	boolean flag = matrix.rowMatrix.get(i).get(pos) == 0;
            if (getNonZeroToPos(matrix.rowMatrix, new SquareMatrix(1).rowMatrix, i, pos, false)) {
            	if (flag) sign = -sign;
                for (int j = i + 1; j < matrix.rows; j++) {
                    reduceTwoRows(matrix.rowMatrix, i, j, pos);
                }
            } else i--;
        	pos++;
        }
        
        double sum = sign;
        for (int i = 0; i < matrix.columns; i++) {
        	sum *= matrix.rowMatrix.get(i).get(i);
        }
        
        return Math.round(sum * 100.0) / 100.0;
	}
	
	// Checks whether the matrix is invertible or not
	public boolean isInvertible() {
		return determinant() != 0;
	}
	
	// Returns the inverse of the matrix
	public Matrix getInverse() {
		if (!isInvertible()) {
			throw new IllegalStateException("Matrix can not be inverted");
		}
		Matrix res = makeIdentity(rows);
		getReducedMatrix(this, res, true);
		return res;
	}

	// Returns the identity matrix of given size
	public Matrix makeIdentity(int size) {
		Matrix res = new SquareMatrix(size);
		for (int i = 0; i < size; i++) {
			List<Double> temp = new ArrayList<>();
			for (int j = 0; j < size; j++) {
				if (i == j) temp.add(1.0);
				else temp.add(0.0);
			}
			res.rowMatrix.add(temp);
		}
		return res;
	}
		
	// Returns the determinant using n! method
	public double determinantSlow() {
		if (rows != columns) {
			throw new IllegalStateException("There is no determinant");
		}
		return determinantSlow(this);
	}

	// Returns the determinant of the matrix
	private double determinantSlow(Matrix matrix) {
		if (matrix.columns == matrix.rows && matrix.columns == 1) {
			return matrix.rowMatrix.get(0).get(0);
		} else {
			double sum = 0.0;
			int sign = 1;
			for (int i = 0; i < matrix.columns; i++) {
				double tempSum = sign * matrix.rowMatrix.get(0).get(i);
				sum += (tempSum * determinantSlow(makeShorterMatrix(matrix, i)));
				sign = -sign;
			}
			return sum;
		}
	}
	
	// Coverts a n * n matrix into a (n - 1) * (n - 1) matrix by removing the given col  
	private Matrix makeShorterMatrix(Matrix toShort, int ignCol) {
		Matrix res = new SquareMatrix(toShort.rows - 1);
		for (int i = 1; i < toShort.rows; i++) {
			List<Double> temp = new ArrayList<>();
			for (int j = 0; j < toShort.columns; j++) {
				if (j != ignCol) temp.add(toShort.rowMatrix.get(i).get(j));
			}
			res.rowMatrix.add(temp);
		}
		return res;
	}

	// Multiply two matrices together
	public Matrix multiply(Matrix other) {
		Matrix res;
		if (rows == other.columns)
			res = new SquareMatrix(rows);
		else
			res = new NonSquareMatrix(rows, other.columns);
		multiply(other, res);
		return res;
	}

	// Clone the matrix
	protected Matrix cloneMatrix(Matrix toClone) {
		Matrix res = new SquareMatrix(toClone.rows);
		super.cloneMatrix(toClone, res);
		return res;
	}

	// Transpose the matrix
	protected Matrix transpose(Matrix matrix) {
		Matrix transpose = new SquareMatrix(matrix.rows);
		transpose(matrix, transpose);
		return transpose;
	}
}