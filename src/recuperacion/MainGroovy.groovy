/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import recuperacion.exceptions.*;
import java.util.logging.Logger;
import recuperacion.JPA.*;


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
        //probarIndexador()
        //probarSVD()
        probarRecuperador()

    }
    static def probarIndexador() {
        Indexador idx = new Indexador();

        idx.guardarDocumento(new File("//Users/octavioruizcastillo/Downloads/empirismo.txt"))
        idx.indexar()
    }
    static def probarRecuperador() {
        Recuperador r = new Recuperador();
        Documento   q = new Documento();
        q.setContenido("reacción al positivismo");
        q.setTipo("CONSULTA")
        r.crearConsulta(q);

        r.busquedaCosenoLSI("OPTIMO", q.getId());
    }
    static def probarSVD() {
        Indexador idx = new Indexador();
        idx.descomposicionSVD()
        idx.guardarIndices(1,1,1)
    }
    
}

