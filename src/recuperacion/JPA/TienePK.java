/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author octavioruizcastillo
 */
@Embeddable
public class TienePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "Documento_id")
    private int documentoid;
    @Basic(optional = false)
    @Column(name = "termino")
    private String termino;

    public TienePK() {
    }

    public TienePK(int documentoid, String termino) {
        this.documentoid = documentoid;
        this.termino = termino;
    }

    public int getDocumentoid() {
        return documentoid;
    }

    public void setDocumentoid(int documentoid) {
        this.documentoid = documentoid;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) documentoid;
        hash += (termino != null ? termino.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TienePK)) {
            return false;
        }
        TienePK other = (TienePK) object;
        if (this.documentoid != other.documentoid) {
            return false;
        }
        if ((this.termino == null && other.termino != null) || (this.termino != null && !this.termino.equals(other.termino))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "recuperacion.JPA.TienePK[documentoid=" + documentoid + ", termino=" + termino + "]";
    }

}
