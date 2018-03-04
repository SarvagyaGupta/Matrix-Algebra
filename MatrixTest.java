import java.util.*;

public class MatrixTest {
	private int rows;
    private int columns;
    private List<List<Double>> rowMatrix;
    private List<List<Double>> reducedMatrix;
    
    public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
	
	private void setRows(int rows) {
		this.rows = rows;
	}

	private void setColumns(int columns) {
		this.columns = columns;
	}
	
	public List<List<Double>> getMatrix() {
		return cloneMatrix(rowMatrix);
	}

	public MatrixTest() {
        rowMatrix = new ArrayList<>();
        reducedMatrix = new ArrayList<>();
        Scanner console = new Scanner(System.in);
        setRows(getData(console, "rows"));
        setColumns(getData(console, "columns"));
        for (int i = 0; i < getRows(); i++) {
            List<Double> temp = new ArrayList<>();
            System.out.println("Currently filling row " + (i + 1));
            for (int j = 0; j < getColumns(); j++) {
                System.out.print("Entry " + (j + 1) + ": ");
                temp.add(round(console.nextDouble()));
            } 
            rowMatrix.add(temp);
        }
        console.close();
        reducedMatrix = getReducedMatrix(rowMatrix);
    }
	
	private MatrixTest(int row, int col) {
		setRows(row);
		setColumns(col);
        rowMatrix = new ArrayList<>();
        reducedMatrix = new ArrayList<>();
	}
	
	private double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
    
    private int getData(Scanner console, String type) {
        System.out.print("How many " + type + " are in the matrix? ");
        int val = console.nextInt();
    	while(val <= 0) {
    		System.out.print("Please enter a number greater than 1: ");
    		val = console.nextInt();
    	}
    	return val;
    }
    
    private List<List<Double>> getReducedMatrix(List<List<Double>> matrixDup) {
    	List<List<Double>> matrix = cloneMatrix(matrixDup);
    	int pos = 0;
        for (int i = 0; i < matrix.size() - 1 && pos < matrix.get(0).size(); i++) {
            if (getNonZeroToPos(matrix, i, pos)) {
                for (int j = i + 1; j < matrix.size(); j++) {
                    reduceTwoRows(matrix, i, j, pos);
                }	
            } else {
            	i--;
            }
        	pos++;
        }
        
        int index = matrix.size();
        if (matrix.size() > matrix.get(0).size()) {
        	pos--;
        	index = matrix.get(0).size();
        }
        
        for (int i = index - 1; i > 0; i--) {
        	for (int j = i - 1; j >= 0; j--) {
        		reduceTwoRows(matrix, i, j, pos);
        	}
        	pos--;
        }
        
        return matrix;
    }
    
    private List<List<Double>> cloneMatrix(List<List<Double>> matrix) {
        List<List<Double>> toReturn = new ArrayList<>();
        for (List<Double> row: matrix) {
            List<Double> temp = new ArrayList<>();
            for (Double entry: row) {
                temp.add(entry);
            }
            toReturn.add(temp);
        }
        return toReturn;
    }
    
    private void reduceTwoRows(List<List<Double>> matrix, int firstRow, int secondRow, 
    		int pos) {
        List<Double> rowOne = matrix.get(firstRow);
        List<Double> rowTwo = matrix.get(secondRow);
        if (rowOne.get(pos) == 0) {
        	return;
        }
        double reduceBy = rowTwo.get(pos) / rowOne.get(pos);
        for (int i = 0; i < rowTwo.size(); i++) {
        	double value = round(rowTwo.get(i) - reduceBy * rowOne.get(i));
            rowTwo.set(i, value);
        }
    }
    
    private boolean getNonZeroToPos(List<List<Double>> matrix, int row, int pos) {
        List<Double> posRow = matrix.get(row);
        double value = posRow.get(pos);
        if (value != 0) {
            return true;
        }
        for (int i = pos + 1; i < matrix.size(); i++) {
            List<Double> temp = matrix.get(i);
            if (temp.get(pos) != 0) {
                swapRows(matrix, pos, i);
                return true;
            }
        }
        return false;
    }
    
    private void swapRows(List<List<Double>> matrix, int firstRow, int secondRow) {
        List<Double> temp = matrix.get(firstRow);
        matrix.set(firstRow, matrix.get(secondRow));
        matrix.set(secondRow, temp);
    }
    
    public void printMatrix() {
        printAnyMatrix(rowMatrix);
    }
    
    public void printReducedMatrix() {
        printAnyMatrix(reducedMatrix);
    }
    
    private void printAnyMatrix(List<List<Double>> matrix) {
        for (List<Double> list: matrix) {
            for (Double entry: list) {
            	if (entry >= 0) {
                	System.out.print("  " + entry);
            	} else {
                	System.out.print(" " + entry);
            	}
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public boolean isOnto() {
    	if (getRows() > getColumns()) {
    		return false;
    	}
    	int count = 0;
    	for (Double num: reducedMatrix.get(getRows() - 1)) {
    		if (num == 0) {
    			count++;
    		}
    	}
    	return count != getColumns();
    }
    
    public boolean isOneToOne() {
    	List<String> solved = solveHomogeneous();
    	for (String entry: solved) {
    		if (entry.contains("" + Double.NaN)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean isInvertible() {
    	return isOnto() && isOneToOne();
    }
    
    public boolean isLinearlyIndependent() {
    	return isOneToOne();
    }
    
    public List<String> solveHomogeneous() {
    	List<Double> toSolve = new ArrayList<>();
    	for (int i = 0; i < getRows(); i++) {
    		toSolve.add(0.0);
    	}
    	return solveSystem(toSolve);
    }
	
	public List<String> solveSystem(List<Double> toSolve) {
		if (toSolve.size() != getRows()) {
			throw new InputMismatchException("Enter a list with size: " + getRows());
		}
		
		List<List<Double>> temp = cloneMatrix(rowMatrix);
		for (int i = 0; i < getRows(); i++) {
			temp.get(i).add(toSolve.get(i));
		}
		temp = getReducedMatrix(temp);
		
		List<String> solved = new ArrayList<>();
		int prevPos = getColumns();
		for (int i = getRows() - 1; i >= 0; i--) {
			int pos = findFirstVal(temp, prevPos, i);
			
			if (pos == prevPos) {
				throw new NoSuchElementException("There is no solution");
			} else if (pos != -1) {
				String res = "";
				for (int j = pos + 1; j < prevPos; j++) {
					solved.add(0, "X" + j + " = " + Double.NaN);
				}
				
				for (int j = pos + 1; j < getColumns(); j++) {
					double val = temp.get(i).get(j);
					res = formatExpression(res, val, j);
				}

				double val = temp.get(i).get(pos);
				double colVal = temp.get(i).get(getColumns());
				res = finalFormat(res, colVal, val, pos);
				solved.add(0, res);
				prevPos = pos;
			}
		}
		
		return solved;
	}
	
	private int findFirstVal(List<List<Double>> temp, int prevPos, int row) {
		for (int j = 0; j <= prevPos; j++) {
			Double entry = temp.get(row).get(j);
			if (entry != 0) {
				return j;
			}
		}
		return -1;
	}
	
	private String formatExpression(String res, double val, int row) {
		if (val != 0) {
			res += "X" + row + " + ";
			if (val != 1) {
				res = val + " * " + res;
			}
		}
		return res;
	}
	
	private String finalFormat(String res, double colVal, double val, int pos) {
		String sign = " - ";
		if (val < 0) {
			colVal = -colVal;
			val = -val;
			sign = " + ";
		}
		if (!res.equals("")) {
			res = res.substring(0, res.length() - 2).trim();
			res = "X" + pos + " = (" + colVal + sign + "(" + res + "))";
		} else {
			res = "X" + pos + " = " + colVal;
		}
		if (val != 1)
			res += " / " + val;
		return res;
	}
	
	public MatrixTest transpose() {
		return transpose(this);
	}
	
	private MatrixTest transpose(MatrixTest matrix) {
		MatrixTest transpose = new MatrixTest(matrix.getColumns(), matrix.getRows());
		for (int i = 0; i < matrix.getColumns(); i++) {
			transpose.rowMatrix.add(new ArrayList<>());
		}
		
		for (int i = 0; i < getColumns(); i++) {
			for (int j = 0; j < getRows(); j++) {
				transpose.rowMatrix.get(i).add(matrix.rowMatrix.get(j).get(i));
			}
		}
		transpose.reducedMatrix = getReducedMatrix(transpose.rowMatrix);
		return transpose;
	}
}