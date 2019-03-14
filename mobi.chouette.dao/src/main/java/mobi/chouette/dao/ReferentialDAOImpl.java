package mobi.chouette.dao;

import mobi.chouette.model.dto.ReferentialInfo;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
public class ReferentialDAOImpl implements ReferentialDAO {

    private static final String SQL_SELECT_SLUG = "SELECT SLUG FROM PUBLIC.REFERENTIALS";
    private static final String SQL_UPDATE_REFERENTIAL="UPDATE public.referentials\n" +
            "SET updated_at=current_timestamp,projection_type=:dataspace_projection,data_format = :dataspace_format\n" +
            "WHERE public.referentials.name=:dataspace_name AND public.referentials.organisation_id = (SELECT organisations.id FROM organisations WHERE organisations.name=:organisation_name)";


    @PersistenceContext(unitName = "public")
    private EntityManager em;

    @Override
    public List<String> getReferentials() {
        Query query = em.createNativeQuery(SQL_SELECT_SLUG);
        return query.getResultList();
    }

    @Override
    public void createReferential(ReferentialInfo referentialInfo) {

        StoredProcedureQuery procedureQuery = em.createStoredProcedureQuery("create_provider_schema");

        procedureQuery.registerStoredProcedureParameter("dest_schema", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_format", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_encrypted_password", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_encrypted_password", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("organisation_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_prefix", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_projection", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_timezone", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_bounds", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns_url", String.class, ParameterMode.IN);

        procedureQuery.setParameter("dest_schema", referentialInfo.getSchemaName());
        procedureQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        procedureQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        procedureQuery.setParameter("admin_user_name", referentialInfo.getAdminUserName());
        procedureQuery.setParameter("admin_user_email", referentialInfo.getAdminUserEmail());
        procedureQuery.setParameter("admin_user_encrypted_password", referentialInfo.getAdminUserInitialEncryptedPassword());
        procedureQuery.setParameter("user_name", referentialInfo.getUserName());
        procedureQuery.setParameter("user_email", referentialInfo.getUserEmail());
        procedureQuery.setParameter("user_encrypted_password", referentialInfo.getUserInitialEncryptedPassword());
        procedureQuery.setParameter("organisation_name", referentialInfo.getOrganisationName());
        procedureQuery.setParameter("dataspace_prefix", referentialInfo.getDataspacePrefix());
        procedureQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        procedureQuery.setParameter("dataspace_timezone", referentialInfo.getDataspaceTimezone());
        procedureQuery.setParameter("dataspace_bounds", referentialInfo.getDataspaceBounds());
        procedureQuery.setParameter("xmlns", referentialInfo.getXmlns());
        procedureQuery.setParameter("xmlns_url", referentialInfo.getXmlnsUrl());

        procedureQuery.execute();
    }

    @Override
    public void createMigratedReferential(ReferentialInfo referentialInfo) {

        StoredProcedureQuery procedureQuery = em.createStoredProcedureQuery("create_rutebanken_schema");

        procedureQuery.registerStoredProcedureParameter("dest_schema", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_format", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("master_organisation_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("master_user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_prefix", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_projection", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_timezone", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_bounds", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns_url", String.class, ParameterMode.IN);

        procedureQuery.setParameter("dest_schema", referentialInfo.getSchemaName());
        procedureQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        procedureQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        procedureQuery.setParameter("admin_user_name", referentialInfo.getAdminUserName());
        procedureQuery.setParameter("master_organisation_name", referentialInfo.getMasterOrganisationName());
        procedureQuery.setParameter("master_user_email", referentialInfo.getMasterUserEmail());
        procedureQuery.setParameter("dataspace_prefix", referentialInfo.getDataspacePrefix());
        procedureQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        procedureQuery.setParameter("dataspace_timezone", referentialInfo.getDataspaceTimezone());
        procedureQuery.setParameter("dataspace_bounds", referentialInfo.getDataspaceBounds());
        procedureQuery.setParameter("xmlns", referentialInfo.getXmlns());
        procedureQuery.setParameter("xmlns_url", referentialInfo.getXmlnsUrl());

        procedureQuery.execute();

    }

    @Override
    public boolean updateReferential(ReferentialInfo referentialInfo) {
        Query updateReferentialQuery = em.createNativeQuery(SQL_UPDATE_REFERENTIAL);
        updateReferentialQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        updateReferentialQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        updateReferentialQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        updateReferentialQuery.setParameter("organisation_name", referentialInfo.getOrganisationName());

        int nbModifiedRow = updateReferentialQuery.executeUpdate();
        return nbModifiedRow != 0;
    }

    @Override
    public boolean updateMigratedReferential(ReferentialInfo referentialInfo) {
        Query updateReferentialQuery = em.createNativeQuery(SQL_UPDATE_REFERENTIAL);
        updateReferentialQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        updateReferentialQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        updateReferentialQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        updateReferentialQuery.setParameter("organisation_name", referentialInfo.getMasterOrganisationName());

        int nbModifiedRow = updateReferentialQuery.executeUpdate();
        return nbModifiedRow != 0;
    }

}