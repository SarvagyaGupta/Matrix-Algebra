import java.util.*;

public interface BasicMatrix {
	public void printMatrix();
	public Matrix addMatrix(Matrix other);
	public Matrix multiply(Matrix other);
	public Matrix getReducedMatrix();
	public boolean isOnto();
	public boolean isOneToOne();
	public boolean isLinearlyIndependent();
	public Matrix transpose();
	public List<List<Double>> getColSpan();
	public List<List<Double>> getRowSpan();
}