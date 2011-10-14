/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import recuperacion.JPA.*;
import org.apache.commons.math.linear.*;
import java.lang.Math;
import org.apache.commons.math.MathRuntimeException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author octavioruizcastillo
 */
class Recuperador extends PreProcesador {

        public void crearConsulta(Documento consulta) {
            doc = consulta
            guardarConsulta();
            lematizar();
            almacenarTerminos()
            def frecuencias = contarFrecuencias()
            
        }
        public def busquedaEuclidiana(Integer consulta) {
            CompletaJpaController jpa = new CompletaJpaController()
            return jpa.ejecutarConsultaEuclidiana(consulta)
        }

        public def busquedaCoseno(Integer consulta) {
            CompletaJpaController jpa = new CompletaJpaController()
            return jpa.ejecutarConsultaCoseno(consulta)
        }

        public def busquedaDice(Integer consulta) {
            CompletaJpaController jpa = new CompletaJpaController()
            return jpa.ejecutarConsultaDice(consulta)
        }

        public def busquedaCosenoLSI(String k, Integer consulta) {
            MatrizSVDJpaController jpa      = new MatrizSVDJpaController();
            DocumentoJpaController document = new DocumentoJpaController();
            def                    res      = []

            try {
                MatrizSVD m = jpa.findIndices(k)
                Integer[] frecs = getFrecDoc(consulta) as Integer[]

                RealMatrix q     = new Array2DRowRealMatrix(frecs as double[][]).transpose();
                RealMatrix u     = new Array2DRowRealMatrix(m.u());
                RealMatrix s     = new RealMatrixImpl(m.s()).inverse();

                RealMatrix q2    = q.multiply(u.multiply(s));
                RealMatrix v    = new RealMatrixImpl(m.v()).transpose();

                def n = 0
                document.findDocumentos().each() { doc ->
                    final def sumNumerador = 0
                    final def sumDenominador1 = 0
                    final def sumDenominador2 = 0
                    for(i in 0..m.getK()-1) {
                        sumNumerador    += q2.getEntry(0,i)*v.getEntry(n, i)
                        sumDenominador1 += Math.pow(q2.getEntry(0,i), 2)
                        sumDenominador2 += Math.pow(v.getEntry(n,i), 2)
                    }
                    final def cos = sumNumerador / (Math.sqrt(sumDenominador1)*Math.sqrt(sumDenominador2))

                    res << [doc.getId(), doc.getContenido(), Math.abs(cos)]

                    n++
                }
                res.sort{a,b-> a[2].equals(b[2])? 0: Math.abs(a[2])<Math.abs(b[2])? -1: 1 }
            } catch(Exception e) {
                Logger.getLogger(Recuperador.class.getName()).log(Level.SEVERE, "No se actualizó LSI después de indexar documentos", e);
            } finally {
                return res
            }
        }
    public def busquedaManhattanLSI(String k, Integer consulta) {
            MatrizSVDJpaController jpa      = new MatrizSVDJpaController();
            DocumentoJpaController document = new DocumentoJpaController();

            MatrizSVD m = jpa.findIndices(k)
            Integer[] frecs = getFrecDoc(consulta) as Integer[]

            RealMatrix q     = new Array2DRowRealMatrix(frecs as double[][]).transpose();
            RealMatrix u     = new Array2DRowRealMatrix(m.u());
            RealMatrix s     = new RealMatrixImpl(m.s()).inverse();
            def        res   = []

            try {
                RealMatrix q2    = q.multiply(u.multiply(s));
                RealMatrix v    = new RealMatrixImpl(m.v()).transpose();

                def n = 0
                document.findDocumentos().each() { doc ->
                    final def sumNumerador = 0
                    for(i in 0..m.getK()-1) {
                        sumNumerador    += Math.abs(q2.getEntry(0,i)-v.getEntry(n, i))
                    }
                    final def cos = sumNumerador

                    res << [doc.getId(), doc.getContenido(), cos]

                    n++
                }
                res.sort{a,b-> a[2].equals(b[2])? 0: Math.abs(a[2])<Math.abs(b[2])? -1: 1 }
            } catch(Exception e) {
                Logger.getLogger(Recuperador.class.getName()).log(Level.SEVERE, "No se actualizó LSI después de indexar documentos", e);
            } finally {
                return res
            }
        }

         public def busquedaEuclidianaLSI(String k, Integer consulta) {
            MatrizSVDJpaController jpa      = new MatrizSVDJpaController();
            DocumentoJpaController document = new DocumentoJpaController();

            MatrizSVD m = jpa.findIndices(k)
            Integer[] frecs = getFrecDoc(consulta) as Integer[]

            RealMatrix q     = new Array2DRowRealMatrix(frecs as double[][]).transpose();
            RealMatrix u     = new Array2DRowRealMatrix(m.u());
            RealMatrix s     = new RealMatrixImpl(m.s()).inverse();
            def        res   = []

            try {
                RealMatrix q2    = q.multiply(u.multiply(s));
                RealMatrix v    = new RealMatrixImpl(m.v()).transpose();

                def n = 0
                document.findDocumentos().each() { doc ->
                    final def sumNumerador = 0

                    for(i in 0..m.getK()-1) {

                        sumNumerador    += Math.pow(q2.getEntry(0,i)-v.getEntry(n, i),2)

                    }
                    final def cos =  Math.sqrt(sumNumerador)

                    res << [doc.getId(), doc.getContenido(), cos]

                    n++
                }
                res.sort{a,b-> a[2].equals(b[2])? 0: Math.abs(a[2])<Math.abs(b[2])? -1: 1 }
            } catch(Exception e) {
                Logger.getLogger(Recuperador.class.getName()).log(Level.SEVERE, "No se actualizó LSI después de indexar documentos", e);
            } finally {
                return res
            }
        }

        public def busquedaProductoInternoLSI(String k, Integer consulta) {
            MatrizSVDJpaController jpa      = new MatrizSVDJpaController();
            DocumentoJpaController document = new DocumentoJpaController();

            MatrizSVD m = jpa.findIndices(k)
            Integer[] frecs = getFrecDoc(consulta) as Integer[]

            RealMatrix q     = new Array2DRowRealMatrix(frecs as double[][]).transpose();
            RealMatrix u     = new Array2DRowRealMatrix(m.u());
            RealMatrix s     = new RealMatrixImpl(m.s()).inverse();
            def        res   = []

            try {
                RealMatrix q2    = q.multiply(u.multiply(s));
                RealMatrix v    = new RealMatrixImpl(m.v()).transpose();

                def n = 0
                document.findDocumentos().each() { doc ->
                    final def sumNumerador = 0

                    for(i in 0..m.getK()-1) {
                        sumNumerador    += q2.getEntry(0,i)*v.getEntry(n, i)

                    }
                    final def cos = sumNumerador

                    res << [doc.getId(), doc.getContenido(), Math.abs(cos)]

                    n++
                }
                res.sort{a,b-> a[2].equals(b[2])? 0: Math.abs(a[2])<Math.abs(b[2])? -1: 1 }
            } catch(Exception e) {
                Logger.getLogger(Recuperador.class.getName()).log(Level.SEVERE, "No se actualizó LSI después de indexar documentos", e);
            } finally {
                return res
            }
        }

        private def getFrecDoc(Integer docID) {
            DocumentoJpaController jpaDoc   = new DocumentoJpaController();
            TieneJpaController     jpaTiene = new TieneJpaController();
            TerminoJpaController   jpaTerm  = new TerminoJpaController();

            def doc   = jpaDoc.findDocumento(docID)
            final def frec = [];

            jpaTerm.findTerminosAlfabeticamente().each() { termino ->
                
                final TienePK tienePK = new TienePK(doc.id, termino.nombre);
                final Tiene   tiene   = jpaTiene.findTiene(tienePK);
                //Se agrega la frecuencia (0 en caso de no existir tupla) al arreglo frec
                if(tiene == null) {
                   frec << 0
                } else {
                   frec << tiene.getFrecuencia()
                }

                //Agrega el arreglo frec al arreglo frecs (bidimensional)
            }
            return frec
        }

        @Override
        private def contarFrecuencias() {
            CompletaJpaController  jpaCompleta = new CompletaJpaController();
            TieneJpaController     jpa         = new TieneJpaController();
            TerminoJpaController   jpaTerm     = new TerminoJpaController();
            DocumentoJpaController jpaDoc      = new DocumentoJpaController();
            
            List terminos = doc.getLematizado().split(" ");
            
            def terms = jpaCompleta.getFrecuenciasConsulta(terminos)

            terms.each() { 
                def termino = it[0]
                def frec    = it[1]
                TienePK pk = new TienePK(doc.getId(), termino);

                if(jpa.findTiene(pk) == null) {
                    final Tiene t = new Tiene(pk);
                    t.setFrecuencia(frec);
                    t.setDocumento(doc);
                    t.setTermino1(jpaTerm.findTermino(termino));
                    jpa.create(t);
                } else {
                    final Tiene t = jpa.findTiene(pk);
                    t.setFrecuencia(frec);
                    jpa.edit(t);
                }
            }
        }
	public void guardarConsulta() {
            DocumentoJpaController jpa = new DocumentoJpaController();
            if(doc.getTipo().equals("CONSULTA")) {
                jpa.create(doc);
            } else {
                throw new Exception("Se intenta guardar un documento como consulta")
            }
        }

        public def findAllConsultas() {
            def res = []
            DocumentoJpaController jpa = new DocumentoJpaController();
            jpa.findConsultas().each {
                res << [it.getId(), it.getContenido()]
            }

            return res
        }
}

