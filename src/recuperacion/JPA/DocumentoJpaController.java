/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.JPA;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import recuperacion.JPA.exceptions.IllegalOrphanException;
import recuperacion.JPA.exceptions.NonexistentEntityException;

/**
 *
 * @author octavioruizcastillo
 */
public class DocumentoJpaController {

    public DocumentoJpaController() {
        emf = Persistence.createEntityManagerFactory("recuperacionPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Documento documento) {
        if (documento.getTieneList() == null) {
            documento.setTieneList(new ArrayList<Tiene>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Tiene> attachedTieneList = new ArrayList<Tiene>();
            for (Tiene tieneListTieneToAttach : documento.getTieneList()) {
                tieneListTieneToAttach = em.getReference(tieneListTieneToAttach.getClass(), tieneListTieneToAttach.getTienePK());
                attachedTieneList.add(tieneListTieneToAttach);
            }
            documento.setTieneList(attachedTieneList);
            em.persist(documento);
            for (Tiene tieneListTiene : documento.getTieneList()) {
                Documento oldDocumentoOfTieneListTiene = tieneListTiene.getDocumento();
                tieneListTiene.setDocumento(documento);
                tieneListTiene = em.merge(tieneListTiene);
                if (oldDocumentoOfTieneListTiene != null) {
                    oldDocumentoOfTieneListTiene.getTieneList().remove(tieneListTiene);
                    oldDocumentoOfTieneListTiene = em.merge(oldDocumentoOfTieneListTiene);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Documento documento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Documento persistentDocumento = em.find(Documento.class, documento.getId());
            List<Tiene> tieneListOld = persistentDocumento.getTieneList();
            List<Tiene> tieneListNew = documento.getTieneList();
            List<String> illegalOrphanMessages = null;
            for (Tiene tieneListOldTiene : tieneListOld) {
                if (!tieneListNew.contains(tieneListOldTiene)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tiene " + tieneListOldTiene + " since its documento field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Tiene> attachedTieneListNew = new ArrayList<Tiene>();
            for (Tiene tieneListNewTieneToAttach : tieneListNew) {
                tieneListNewTieneToAttach = em.getReference(tieneListNewTieneToAttach.getClass(), tieneListNewTieneToAttach.getTienePK());
                attachedTieneListNew.add(tieneListNewTieneToAttach);
            }
            tieneListNew = attachedTieneListNew;
            documento.setTieneList(tieneListNew);
            documento = em.merge(documento);
            for (Tiene tieneListNewTiene : tieneListNew) {
                if (!tieneListOld.contains(tieneListNewTiene)) {
                    Documento oldDocumentoOfTieneListNewTiene = tieneListNewTiene.getDocumento();
                    tieneListNewTiene.setDocumento(documento);
                    tieneListNewTiene = em.merge(tieneListNewTiene);
                    if (oldDocumentoOfTieneListNewTiene != null && !oldDocumentoOfTieneListNewTiene.equals(documento)) {
                        oldDocumentoOfTieneListNewTiene.getTieneList().remove(tieneListNewTiene);
                        oldDocumentoOfTieneListNewTiene = em.merge(oldDocumentoOfTieneListNewTiene);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = documento.getId();
                if (findDocumento(id) == null) {
                    throw new NonexistentEntityException("The documento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Documento documento;
            try {
                documento = em.getReference(Documento.class, id);
                documento.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tiene> tieneListOrphanCheck = documento.getTieneList();
            for (Tiene tieneListOrphanCheckTiene : tieneListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Documento (" + documento + ") cannot be destroyed since the Tiene " + tieneListOrphanCheckTiene + " in its tieneList field has a non-nullable documento field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(documento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Documento> findDocumentoEntities() {
        return findDocumentoEntities(true, -1, -1);
    }

    public List<Documento> findDocumentoEntities(int maxResults, int firstResult) {
        return findDocumentoEntities(false, maxResults, firstResult);
    }

    private List<Documento> findDocumentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Documento.class));
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

    public List<Documento> findDocumentos() {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNamedQuery("Documento.findByTipo");
            cq.setParameter("tipo", "DOCUMENTO");

            return cq.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Documento> findConsultas() {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNamedQuery("Documento.findByTipo");
            cq.setParameter("tipo", "CONSULTA");

            return cq.getResultList();
        } finally {
            em.close();
        }
    }

    public Object[] findFrecuencias(Integer query) {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createNativeQuery("SELECT c.frecuencia FROM Completa c WHERE c.id = ? ORDER BY c.termino ASC");
            cq.setParameter(1, query);

            return cq.getResultList().toArray();
        } finally {
            em.close();
        }
    }

    public Documento findDocumento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Documento.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Documento> rt = cq.from(Documento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
