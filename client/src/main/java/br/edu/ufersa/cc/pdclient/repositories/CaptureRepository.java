package br.edu.ufersa.cc.pdclient.repositories;

import java.util.List;

import br.edu.ufersa.cc.pdclient.entities.Capture;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CaptureRepository {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("captures");

    public List<Capture> listAll() {
        final var em = emf.createEntityManager();
        final var query = em.createQuery("select c from Capture c", Capture.class);
        final var result = query.getResultList();

        em.close();
        return result;
    }

    public List<Capture> listByRegion(final String region) {
        final var em = emf.createEntityManager();
        final var query = em.createQuery(
                "SELECT c FROM Capture c WHERE c.region = :region", Capture.class);
        query.setParameter("region", region);

        final var result = query.getResultList();
        em.close();
        return result;
    }

    public long countAll() {
        final var em = emf.createEntityManager();
        final var count = em.createQuery("SELECT COUNT(c) FROM Capture c", Long.class)
                .getSingleResult();
        em.close();
        return count;
    }

    public void create(final Capture capture) {
        final var em = emf.createEntityManager();
        em.getTransaction().begin();

        em.persist(capture);

        em.getTransaction().commit();
        em.close();
    }

}
