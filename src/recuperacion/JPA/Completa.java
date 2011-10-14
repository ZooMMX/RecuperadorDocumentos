/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author octavioruizcastillo
 */
@Entity
@Table(name = "completa")
@NamedQueries({
    @NamedQuery(name = "Completa.findAll", query = "SELECT c FROM Completa c"),
    @NamedQuery(name = "Completa.findById", query = "SELECT c FROM Completa c WHERE c.id = :id"),
    @NamedQuery(name = "Completa.findByTipo", query = "SELECT c FROM Completa c WHERE c.tipo = :tipo"),
    @NamedQuery(name = "Completa.findByTermino", query = "SELECT c FROM Completa c WHERE c.termino = :termino"),
    @NamedQuery(name = "Completa.findByFrecuencia", query = "SELECT c FROM Completa c WHERE c.frecuencia = :frecuencia"),
    @NamedQuery(name = "Completa.frecuenciasConsulta", query = "SELECT c.termino as termino, max(c.frecuencia) as frecuencia FROM Completa as c WHERE c.termino IN :terminos GROUP BY c.termino")
    })
public class Completa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "id")
    @Id
    private int id;
    @Lob
    @Column(name = "contenido")
    private String contenido;
    @Lob
    @Column(name = "lematizado")
    private String lematizado;
    @Lob
    @Column(name = "ruta")
    private String ruta;
    @Basic(optional = false)
    @Column(name = "tipo")
    private String tipo;
    @Basic(optional = false)
    @Column(name = "termino")
    private String termino;
    @Column(name = "frecuencia")
    private Integer frecuencia;

    public Completa() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getLematizado() {
        return lematizado;
    }

    public void setLematizado(String lematizado) {
        this.lematizado = lematizado;
    }
    
    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public Integer getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Integer frecuencia) {
        this.frecuencia = frecuencia;
    }

}
