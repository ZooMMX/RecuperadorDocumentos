/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import java.util.List;
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
public class MatrizSVDJpaController {

    public MatrizSVDJpaController() {
        emf = Persistence.createEntityManagerFactory("recuperacionPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MatrizSVD indices) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(indices);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIndices(indices.getIndice()) != null) {
                throw new PreexistingEntityException("Indices " + indices + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MatrizSVD indices) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            indices = em.merge(indices);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = indices.getIndice();
                if (findIndices(id) == null) {
                    throw new NonexistentEntityException("The indices with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MatrizSVD indices;
            try {
                indices = em.getReference(MatrizSVD.class, id);
                indices.getIndice();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The indices with id " + id + " no longer exists.", enfe);
            }
            em.remove(indices);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MatrizSVD> findIndicesEntities() {
        return findIndicesEntities(true, -1, -1);
    }

    public List<MatrizSVD> findIndicesEntities(int maxResults, int firstResult) {
        return findIndicesEntities(false, maxResults, firstResult);
    }

    private List<MatrizSVD> findIndicesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MatrizSVD.class));
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

    public MatrizSVD findIndices(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MatrizSVD.class, id);
        } finally {
            em.close();
        }
    }

    public int getIndicesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MatrizSVD> rt = cq.from(MatrizSVD.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
