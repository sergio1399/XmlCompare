package app.components.dao;

import app.components.model.XMLError;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class XmlErrorDAO {

    /** Поле менеджера entity-объектов */
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveError(XMLError error){
        entityManager.persist(error);
    }

}
