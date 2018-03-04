import java.util.*;

public class MatrixMain {
    public static void main(String[] args) {
       SquareMatrix curr = new SquareMatrix(3);
       // Matrix other = new Matrix();
       List<Double> list = new ArrayList<Double>();
       list.add(9.0);
       list.add(-5.0);
       list.add(-5.0);
       System.out.println("Current Matrix:");
       curr.printMatrix();
       Matrix reducedCurr = curr.getReducedMatrix();
       System.out.println("Reduced Matrix:");
       reducedCurr.printMatrix();
       System.out.println("Is Onto: " + curr.isOnto());
       System.out.println("Is OneToOne: " + curr.isOneToOne());
       System.out.println("Is Invertible: " + curr.isInvertible());
       System.out.println("Is Linearly Independent: " + curr.isLinearlyIndependent());
       System.out.println("Homogeneous Solution: " + curr.solveHomogeneous());
       System.out.println("Particular solution: " + curr.solveSystem(list));
       System.out.println("Transposed Matrix:");
       curr.transpose().printMatrix();
       System.out.println("Determinant: " + curr.determinantSlow());
       System.out.println("Determinant: " + curr.determinant());
       /*Matrix multiplied1 = curr.multiply(other);
       Matrix multiplied2 = other.multiply(curr);
       multiplied1.addMatrix(multiplied1.makeIdentity(4));
       multiplied2.addMatrix(multiplied1.makeIdentity(3));
       System.out.println("Determinant: " + multiplied1.determinantFast());
       System.out.println("Determinant: " + multiplied2.determinantFast());
       System.out.println("Multiplied: ");
       multiplied.printMatrix();*/
       System.out.println("Inverse: ");
       curr.getInverse().printMatrix();
    }
}