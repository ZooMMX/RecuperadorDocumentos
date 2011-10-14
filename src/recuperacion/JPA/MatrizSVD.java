/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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
@Table(name = "MatrizSVD")
@NamedQueries({
    @NamedQuery(name = "MatrizSVD.findAll", query = "SELECT i FROM MatrizSVD i"),
    @NamedQuery(name = "MatrizSVD.findByIndice", query = "SELECT i FROM MatrizSVD i WHERE i.indice = :indice")})
public class MatrizSVD implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "indice")
    private String indice;
    @Basic(optional = false)
    @Lob
    @Column(name = "u")
    private byte[] u;
    @Basic(optional = false)
    @Lob
    @Column(name = "s")
    private byte[] s;
    @Basic(optional = false)
    @Lob
    @Column(name = "v")
    private byte[] v;
    @Column(name = "k")
    private Integer k;

    public MatrizSVD() {
    }

    public MatrizSVD(String indice) {
        this.indice = indice;
    }

    public MatrizSVD(String indice, byte[] u, byte[] s, byte[] v) {
        this.indice = indice;
        this.u = u;
        this.s = s;
        this.v = v;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public byte[] getU() {
        return u;
    }

    public void setU(byte[] u) {
        this.u = u;
    }

    public byte[] getS() {
        return s;
    }

    public void setS(byte[] s) {
        this.s = s;
    }

    public byte[] getV() {
        return v;
    }

    public void setV(byte[] v) {
        this.v = v;
    }

    public double[][] s() {
        return recuperacion.Indexador.deSerializar(getS());
    }
    public double[][] v() {
        return recuperacion.Indexador.deSerializar(getV());
    }
    public double[][] u() {
        return recuperacion.Indexador.deSerializar(getU());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (indice != null ? indice.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MatrizSVD)) {
            return false;
        }
        MatrizSVD other = (MatrizSVD) object;
        if ((this.indice == null && other.indice != null) || (this.indice != null && !this.indice.equals(other.indice))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "recuperacion.JPA.Indices[indice=" + indice + "]";
    }

}
