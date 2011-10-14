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
public class TieneJpaController {

    public TieneJpaController() {
        emf = Persistence.createEntityManagerFactory("recuperacionPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tiene tiene) throws PreexistingEntityException, Exception {
        if (tiene.getTienePK() == null) {
            tiene.setTienePK(new TienePK());
        }
        tiene.getTienePK().setTermino(tiene.getTermino1().getNombre());
        tiene.getTienePK().setDocumentoid(tiene.getDocumento().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Documento documento = tiene.getDocumento();
            if (documento != null) {
                documento = em.getReference(documento.getClass(), documento.getId());
                tiene.setDocumento(documento);
            }
            Termino termino1 = tiene.getTermino1();
            if (termino1 != null) {
                termino1 = em.getReference(termino1.getClass(), termino1.getNombre());
                tiene.setTermino1(termino1);
            }
            em.persist(tiene);
            if (documento != null) {
                documento.getTieneList().add(tiene);
                documento = em.merge(documento);
            }
            if (termino1 != null) {
                termino1.getTieneList().add(tiene);
                termino1 = em.merge(termino1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTiene(tiene.getTienePK()) != null) {
                throw new PreexistingEntityException("Tiene " + tiene + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tiene tiene) throws NonexistentEntityException, Exception {
        tiene.getTienePK().setTermino(tiene.getTermino1().getNombre());
        tiene.getTienePK().setDocumentoid(tiene.getDocumento().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tiene persistentTiene = em.find(Tiene.class, tiene.getTienePK());
            Documento documentoOld = persistentTiene.getDocumento();
            Documento documentoNew = tiene.getDocumento();
            Termino termino1Old = persistentTiene.getTermino1();
            Termino termino1New = tiene.getTermino1();
            if (documentoNew != null) {
                documentoNew = em.getReference(documentoNew.getClass(), documentoNew.getId());
                tiene.setDocumento(documentoNew);
            }
            if (termino1New != null) {
                termino1New = em.getReference(termino1New.getClass(), termino1New.getNombre());
                tiene.setTermino1(termino1New);
            }
            tiene = em.merge(tiene);
            if (documentoOld != null && !documentoOld.equals(documentoNew)) {
                documentoOld.getTieneList().remove(tiene);
                documentoOld = em.merge(documentoOld);
            }
            if (documentoNew != null && !documentoNew.equals(documentoOld)) {
                documentoNew.getTieneList().add(tiene);
                documentoNew = em.merge(documentoNew);
            }
            if (termino1Old != null && !termino1Old.equals(termino1New)) {
                termino1Old.getTieneList().remove(tiene);
                termino1Old = em.merge(termino1Old);
            }
            if (termino1New != null && !termino1New.equals(termino1Old)) {
                termino1New.getTieneList().add(tiene);
                termino1New = em.merge(termino1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TienePK id = tiene.getTienePK();
                if (findTiene(id) == null) {
                    throw new NonexistentEntityException("The tiene with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TienePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tiene tiene;
            try {
                tiene = em.getReference(Tiene.class, id);
                tiene.getTienePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tiene with id " + id + " no longer exists.", enfe);
            }
            Documento documento = tiene.getDocumento();
            if (documento != null) {
                documento.getTieneList().remove(tiene);
                documento = em.merge(documento);
            }
            Termino termino1 = tiene.getTermino1();
            if (termino1 != null) {
                termino1.getTieneList().remove(tiene);
                termino1 = em.merge(termino1);
            }
            em.remove(tiene);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tiene> findTieneEntities() {
        return findTieneEntities(true, -1, -1);
    }

    public List<Tiene> findTieneEntities(int maxResults, int firstResult) {
        return findTieneEntities(false, maxResults, firstResult);
    }

    private List<Tiene> findTieneEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tiene.class));
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

    public Tiene findTiene(TienePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tiene.class, id);
        } finally {
            em.close();
        }
    }

    public int getTieneCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tiene> rt = cq.from(Tiene.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
