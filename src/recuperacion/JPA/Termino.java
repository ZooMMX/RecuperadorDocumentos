/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author octavioruizcastillo
 */
@Entity
@Table(name = "Termino")
@NamedQueries({
    @NamedQuery(name = "Termino.findAll", query = "SELECT t FROM Termino t"),
    @NamedQuery(name = "Termino.findByNombre", query = "SELECT t FROM Termino t WHERE t.nombre = :nombre")
    })
public class Termino implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "termino1")
    private List<Tiene> tieneList;

    public Termino() {
    }

    public Termino(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Tiene> getTieneList() {
        return tieneList;
    }

    public void setTieneList(List<Tiene> tieneList) {
        this.tieneList = tieneList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Termino)) {
            return false;
        }
        Termino other = (Termino) object;
        if ((this.nombre == null && other.nombre != null) || (this.nombre != null && !this.nombre.equals(other.nombre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "recuperacion.JPA.Termino[nombre=" + nombre + "]";
    }

}
