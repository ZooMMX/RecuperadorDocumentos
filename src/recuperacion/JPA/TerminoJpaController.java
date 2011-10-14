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
import recuperacion.exceptions.IllegalOrphanException;
import recuperacion.exceptions.NonexistentEntityException;
import recuperacion.exceptions.PreexistingEntityException;

/**
 *
 * @author octavioruizcastillo
 */
public class TerminoJpaController {

    public TerminoJpaController() {
        emf = Persistence.createEntityManagerFactory("recuperacionPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Termino termino) throws PreexistingEntityException, Exception {
        if (termino.getTieneList() == null) {
            termino.setTieneList(new ArrayList<Tiene>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Tiene> attachedTieneList = new ArrayList<Tiene>();
            for (Tiene tieneListTieneToAttach : termino.getTieneList()) {
                tieneListTieneToAttach = em.getReference(tieneListTieneToAttach.getClass(), tieneListTieneToAttach.getTienePK());
                attachedTieneList.add(tieneListTieneToAttach);
            }
            termino.setTieneList(attachedTieneList);
            em.persist(termino);
            for (Tiene tieneListTiene : termino.getTieneList()) {
                Termino oldTermino1OfTieneListTiene = tieneListTiene.getTermino1();
                tieneListTiene.setTermino1(termino);
                tieneListTiene = em.merge(tieneListTiene);
                if (oldTermino1OfTieneListTiene != null) {
                    oldTermino1OfTieneListTiene.getTieneList().remove(tieneListTiene);
                    oldTermino1OfTieneListTiene = em.merge(oldTermino1OfTieneListTiene);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTermino(termino.getNombre()) != null) {
                throw new PreexistingEntityException("Termino " + termino + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Termino termino) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Termino persistentTermino = em.find(Termino.class, termino.getNombre());
            List<Tiene> tieneListOld = persistentTermino.getTieneList();
            List<Tiene> tieneListNew = termino.getTieneList();
            List<String> illegalOrphanMessages = null;
            for (Tiene tieneListOldTiene : tieneListOld) {
                if (!tieneListNew.contains(tieneListOldTiene)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tiene " + tieneListOldTiene + " since its termino1 field is not nullable.");
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
            termino.setTieneList(tieneListNew);
            termino = em.merge(termino);
            for (Tiene tieneListNewTiene : tieneListNew) {
                if (!tieneListOld.contains(tieneListNewTiene)) {
                    Termino oldTermino1OfTieneListNewTiene = tieneListNewTiene.getTermino1();
                    tieneListNewTiene.setTermino1(termino);
                    tieneListNewTiene = em.merge(tieneListNewTiene);
                    if (oldTermino1OfTieneListNewTiene != null && !oldTermino1OfTieneListNewTiene.equals(termino)) {
                        oldTermino1OfTieneListNewTiene.getTieneList().remove(tieneListNewTiene);
                        oldTermino1OfTieneListNewTiene = em.merge(oldTermino1OfTieneListNewTiene);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = termino.getNombre();
                if (findTermino(id) == null) {
                    throw new NonexistentEntityException("The termino with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Termino termino;
            try {
                termino = em.getReference(Termino.class, id);
                termino.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The termino with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tiene> tieneListOrphanCheck = termino.getTieneList();
            for (Tiene tieneListOrphanCheckTiene : tieneListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Termino (" + termino + ") cannot be destroyed since the Tiene " + tieneListOrphanCheckTiene + " in its tieneList field has a non-nullable termino1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(termino);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Termino> findTerminoEntities() {
        return findTerminoEntities(true, -1, -1);
    }

    public List<Termino> findTerminoEntities(int maxResults, int firstResult) {
        return findTerminoEntities(false, maxResults, firstResult);
    }

    private List<Termino> findTerminoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Termino.class));
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

    public List<Termino> findTerminosAlfabeticamente() {
        EntityManager em = getEntityManager();
        try {
            Query cq = em.createQuery("SELECT t FROM Termino t ORDER BY t.nombre asc");

            return cq.getResultList();
        } finally {
            em.close();
        }
    }

    public Termino findTermino(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Termino.class, id);
        } finally {
            em.close();
        }
    }

    public int getTerminoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Termino> rt = cq.from(Termino.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
