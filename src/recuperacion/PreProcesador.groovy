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
    public Documento doc;

    public def lematizar() {
        DocumentoJpaController jpa     = new DocumentoJpaController();
        SpanishStemmer         stem    = new SpanishStemmer();

        String docLimpio     = doc.getContenido()
        String docLematizado = "";

        docLimpio = (docLimpio =~ "[^A-Za-z0-9 ]").replaceAll(" ") //Transforma todos los símbolos en espacios
        docLimpio = (docLimpio =~ "[ \t]{2,}").replaceAll(" ") //Remueve espacios excedentes

        docLimpio.split(" ").each {
            stem.setCurrent(it)
            stem.stem()
            docLematizado += stem.getCurrent() + " ";
        }
        doc.setLematizado(docLematizado);
        jpa.edit(doc);
    }

    public def almacenarTerminos() {
        TerminoJpaController jpa = new TerminoJpaController();

        doc.getLematizado().split(" ").each() {

            if(jpa.findTermino(it) == null) {
                final Termino t = new Termino();
                t.setNombre(it);
                jpa.create(t);

            }
        }
    }

    public def contarFrecuencias() {

        TieneJpaController   jpa     = new TieneJpaController();
        TerminoJpaController jpaTerm = new TerminoJpaController();

        doc.getLematizado().split(" ").each() { termino ->
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

