/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion;

import org.apache.commons.math.linear.SingularValueDecompositionImpl;


public class Util {
    public double[][] u;
    public double[][] s;
    public double[][] v;

    public static Integer uColLength(SingularValueDecompositionImpl svd){
        
        return svd.getU().getColumn(0).length;
    }

    public void recortarSVD(SingularValueDecompositionImpl svd, Integer k) {
            Integer uLength = uColLength(svd);
            u = new double[uLength][k];
            s = new double[k][k];
            v = new double[k][svd.getV().getColumn(0).length];

            svd.getU().copySubMatrix(0,uLength-1,0,k-1,u);
            svd.getS().copySubMatrix(0,k-1,0,k-1,s);
            svd.getV().copySubMatrix(0,k-1,0,svd.getV().getRow(0).length-1,v);

            System.out.println("nada");
    }
}
