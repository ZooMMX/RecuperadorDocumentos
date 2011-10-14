/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import recuperacion.JPA.*;
import snowball.*;

/**
 *
 * @author octavioruizcastillo
 */
public class PreProcesador {
    public    Documento         doc;
    protected ArrayList<String> terminos;

    public def lematizar() {
        DocumentoJpaController jpa       = new DocumentoJpaController();
        SpanishStemmer         stem      = new SpanishStemmer();
        def                    contenido;

        terminos  = new ArrayList<String>();

        if(doc.getTipo()=="CONSULTA") {
            contenido = doc.getContenido()
        } else {
            contenido = new File(doc.getRuta())
        }

        contenido.eachLine() { linea ->
            final String docLimpio = linea;
            docLimpio = (docLimpio =~ "[^A-Za-z0-9áéíóúÁÉÍÓÚñÑ ]").replaceAll(" ") //Transforma todos los símbolos en espacios
            docLimpio = (docLimpio =~ "[ \t]{2,}").replaceAll(" ") //Remueve espacios excedentes

            docLimpio.split(" ").each() { palabra ->
                stem.setCurrent(palabra)
                stem.stem()
                final lexema = stem.getCurrent()
                if(lexema.length() > 3) {
                    terminos.add(lexema)
                }
            }

        }
        if(doc.getTipo()=="CONSULTA") {
            doc.setLematizado(terminos.join(" "))
            jpa.edit(doc)
        }
        
    }

    public def almacenarTerminos() {
        TerminoJpaController jpa = new TerminoJpaController();

        terminos.each() {
            if(jpa.findTermino(it) == null) {
                final Termino t = new Termino();
                t.setNombre(it);
                jpa.create(t);

            }
        }
    }

    protected def contarFrecuencias() {

        TieneJpaController   jpa     = new TieneJpaController();
        TerminoJpaController jpaTerm = new TerminoJpaController();

        terminos.each() { termino ->
            TienePK pk = new TienePK(doc.getId(), termino);

            if(jpa.findTiene(pk) == null) {
                final Tiene t = new Tiene(pk);
                t.setFrecuencia(1);
                t.setDocumento(doc);
                t.setTermino1(jpaTerm.findTermino(termino));
                jpa.create(t);
            } else {
                final Tiene t = jpa.findTiene(pk);
                t.setFrecuencia(1+t.getFrecuencia());
                jpa.edit(t);
            }
        }

    }
}

