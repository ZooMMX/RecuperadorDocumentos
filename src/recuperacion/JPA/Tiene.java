/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author octavioruizcastillo
 */
@Entity
@Table(name = "Tiene")
@NamedQueries({
    @NamedQuery(name = "Tiene.findAll", query = "SELECT t FROM Tiene t"),
    @NamedQuery(name = "Tiene.findByDocumentoid", query = "SELECT t FROM Tiene t WHERE t.tienePK.documentoid = :documentoid"),
    @NamedQuery(name = "Tiene.findByTermino", query = "SELECT t FROM Tiene t WHERE t.tienePK.termino = :termino"),
    @NamedQuery(name = "Tiene.findByFrecuencia", query = "SELECT t FROM Tiene t WHERE t.frecuencia = :frecuencia")})
public class Tiene implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TienePK tienePK;
    @Column(name = "frecuencia")
    private Integer frecuencia;
    @JoinColumn(name = "Documento_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Documento documento;
    @JoinColumn(name = "termino", referencedColumnName = "nombre", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Termino termino1;

    public Tiene() {
    }

    public Tiene(TienePK tienePK) {
        this.tienePK = tienePK;
    }

    public Tiene(int documentoid, String termino) {
        this.tienePK = new TienePK(documentoid, termino);
    }

    public TienePK getTienePK() {
        return tienePK;
    }

    public void setTienePK(TienePK tienePK) {
        this.tienePK = tienePK;
    }

    public Integer getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Integer frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Termino getTermino1() {
        return termino1;
    }

    public void setTermino1(Termino termino1) {
        this.termino1 = termino1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tienePK != null ? tienePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tiene)) {
            return false;
        }
        Tiene other = (Tiene) object;
        if ((this.tienePK == null && other.tienePK != null) || (this.tienePK != null && !this.tienePK.equals(other.tienePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "recuperacion.JPA.Tiene[tienePK=" + tienePK + "]";
    }

}
