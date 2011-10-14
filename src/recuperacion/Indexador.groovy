/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import recuperacion.JPA.*;
import snowball.*;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author octavioruizcastillo
 */
class Indexador extends PreProcesador {

    public void indexar() {
        lematizar();
        almacenarTerminos()
        contarFrecuencias()

    }

    public void guardarDocumento(File txtFile) {
        DocumentoJpaController jpa = new DocumentoJpaController()

        doc = new Documento()
        txtFile.withReader { doc.setContenido(it.readLine()+it.readLine()) } //Guarda las primeras líneas del documento en BD
        doc.setTipo("DOCUMENTO")
        doc.setRuta(txtFile.path)
        jpa.create(doc)
    }

    private SingularValueDecompositionImpl descomposicionSVD() {
        try {
            System.out.println("check");
            def frecs = getTablaFrecuencias()
            RealMatrix rm = new Array2DRowRealMatrix(frecs as double[][]);
            SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(rm);

            return svd;
        } catch(Exception e) {
            Logger.getLogger(Indexador.class.getName()).log(Level.SEVERE, "Base de datos vacía", e);
            return null;
        } catch(Error err) {
            Logger.getLogger(Indexador.class.getName()).log(Level.SEVERE, "Error: Memoria JVM llena", err);
        }

    }
    void guardarIndices(int mayor, int optimo, int menor){

        SingularValueDecompositionImpl svd = descomposicionSVD();
        MatrizSVDJpaController jpa = new MatrizSVDJpaController();

        ['mayor':mayor, 'optimo':optimo, 'menor':menor].each(){ i, it ->

            final usv = new Util();
            usv.recortarSVD(svd, it);

            if(jpa.findIndices(i)!=null) { jpa.destroy(i) }
            final MatrizSVD indice = new MatrizSVD(i, serializar(usv.u), serializar(usv.s), serializar(usv.v));
            indice.setK(it)
            jpa.create(indice); //Guardo los registros

        }

    }

    byte[] serializar(double[][] d) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutputStream oStream = new ObjectOutputStream( bStream );
        oStream.writeObject(d);
        byte[] byteVal = bStream. toByteArray();
        oStream.close();
        bStream.close();
        return byteVal;
    }
    static double[][] deSerializar(byte[] b) {
        ByteArrayInputStream bStream = new ByteArrayInputStream(b);
        ObjectInputStream oStream = new ObjectInputStream(bStream);
        double[][] d = oStream.readObject();
        oStream.close()
        bStream.close()
        return d
    }

    private def getTablaFrecuencias(Boolean completa = false) {
        DocumentoJpaController jpaDoc   = new DocumentoJpaController();
        TieneJpaController     jpaTiene = new TieneJpaController();
        TerminoJpaController   jpaTerm  = new TerminoJpaController();

        int nDocs = jpaDoc.getDocumentoCount();
        def frecs = []

        def documentos = completa ? jpaDoc.findDocumentoEntities() : jpaDoc.findDocumentos()

        jpaTerm.findTerminosAlfabeticamente().each() { termino ->
            final def frec = [];
            documentos.each() { doc ->
                final TienePK tienePK = new TienePK(doc.id, termino.nombre);
                final Tiene   tiene   = jpaTiene.findTiene(tienePK);
                //Se agrega la frecuencia (0 en caso de no existir tupla) al arreglo frec
                if(tiene == null) {
                   frec << 0
                } else {
                   frec << tiene.getFrecuencia()
                }
            }
            //Agrega el arreglo frec al arreglo frecs (bidimensional)
            if(completa) { frec = [termino.nombre] + frec }
            frecs << frec;
        }
        return frecs
    }

}

