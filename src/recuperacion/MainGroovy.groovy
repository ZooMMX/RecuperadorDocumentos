/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import recuperacion.exceptions.*;
import java.util.logging.Logger;


/**
 *
 * @author octavioruizcastillo
 */
class MainGroovy {
    public static void main(String[] args) {
        configExceptions();
        def v = new Vista()
        v.gui();
        //prueba()
    }

    static def configExceptions() {
        Thread.setDefaultUncaughtExceptionHandler(new UEHandler());
        Logger.getLogger("").addHandler(new CEHandler());
    }

    static def prueba() {
        Indexador idx = new Indexador();
        
        idx.guardarDocumento(new File("/Users/octavioruizcastillo/Downloads/commons-math-2.2/NOTICE.txt"))
        idx.indexar()
        /*
        idx.descomposicionSVD()
        idx.guardarIndices(9,9,9)

        Recuperador r = new Recuperador();
        r.busquedaCosenoLSI("OPTIMO", 180);
        */
    }

    
}

