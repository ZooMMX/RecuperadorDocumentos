/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import recuperacion.JPA.exceptions.NonexistentEntityException;
import recuperacion.JPA.exceptions.PreexistingEntityException;

/**
 *
 * @author octavioruizcastillo
 */
public class CompletaJpaController {

    public CompletaJpaController() {
        emf = Persistence.createEntityManagerFactory("recuperacionPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    public List getFrecuenciasConsulta(List terminos) {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNamedQuery("Completa.frecuenciasConsulta");
            cq.setParameter("terminos", terminos);

            return cq.getResultList();
        } finally {
            em.close();
        }
    }
    public List ejecutarConsultaEuclidiana(Integer idQuery) {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNativeQuery("SELECT d.id, SUBSTRING(d.contenido,1,50) as contenido, SQRT( SUM( POW( q.Frecuencia - d.Frecuencia, 2 ) ) ) as valor FROM Completa q, Completa d WHERE q.termino = d.termino AND d.tipo = 'DOCUMENTO' AND q.id = ? GROUP BY d.id ORDER BY valor");

            cq.setParameter(1, idQuery);

            return cq.getResultList();
        } finally {
            em.close();
        }
    }
    public List ejecutarConsultaCoseno(Integer idQuery) {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNativeQuery("select d.Id, SUBSTRING(d.contenido,1,50) as contenido, sum(q.Frecuencia*d.Frecuencia)/(sqrt(sum(pow(q.Frecuencia,2)))*sqrt(sum(pow(d.Frecuencia,2)))) as valor from completa q, completa d where q.termino=d.termino and d.tipo='DOCUMENTO' and q.Id = ? GROUP BY d.id ORDER BY valor");

            cq.setParameter(1, idQuery);

            return cq.getResultList();
        } finally {
            em.close();
        }
    }
    public List ejecutarConsultaDice(Integer idQuery) {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNativeQuery("select d.Id, SUBSTRING(d.contenido,1,50) as contenido, (2*sum(q.frecuencia * d.frecuencia))/(sum(pow(q.frecuencia,2))+ sum(pow(d.frecuencia,2))) as valor from completa q, completa d where q.termino=d.termino and q.Id=? and d.tipo = 'DOCUMENTO' group by d.Id order by valor asc");

            cq.setParameter(1, idQuery);

            return cq.getResultList();
        } finally {
            em.close();
        }
    }

    public void create(Completa completa) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(completa);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCompleta(completa.getId()) != null) {
                throw new PreexistingEntityException("Completa " + completa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Completa completa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            completa = em.merge(completa);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = completa.getId();
                if (findCompleta(id) == null) {
                    throw new NonexistentEntityException("The completa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Completa completa;
            try {
                completa = em.getReference(Completa.class, id);
                completa.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The completa with id " + id + " no longer exists.", enfe);
            }
            em.remove(completa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Completa> findCompletaEntities() {
        return findCompletaEntities(true, -1, -1);
    }

    public List<Completa> findCompletaEntities(int maxResults, int firstResult) {
        return findCompletaEntities(false, maxResults, firstResult);
    }

    private List<Completa> findCompletaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Completa.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Completa findCompleta(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Completa.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompletaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Completa> rt = cq.from(Completa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
