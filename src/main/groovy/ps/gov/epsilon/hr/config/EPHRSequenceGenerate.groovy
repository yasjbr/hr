package ps.gov.epsilon.hr.config

import grails.core.DefaultGrailsClass
import org.grails.core.artefact.DomainClassArtefactHandler
import org.hibernate.HibernateException
import org.hibernate.dialect.Dialect
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.id.IdentifierGenerator
import org.hibernate.id.PersistentIdentifierGenerator
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.type.Type
import org.springframework.web.context.request.RequestContextHolder
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.police.common.utils.v1.PCPSessionUtils
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Created by wassi on 20/04/17.
 */
class EPHRSequenceGenerate extends SequenceStyleGenerator implements IdentifierGenerator {

    public void configure(Type type, Properties params, Dialect dialect) {
        String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
        String schemaName = params.getProperty("schemaName");
        if (schemaName != null) {
            params.setProperty(PersistentIdentifierGenerator.SCHEMA, schemaName);
        }
        if (tableName != null) {
            params.setProperty(SEQUENCE_PARAM, "seq_" + tableName);
        }
        super.configure(type, params, dialect);
    }

    Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        try {
            def parentClass = object?.class
            while(parentClass?.superclass != null && DomainClassArtefactHandler.isDomainClass(parentClass?.superclass) && !(parentClass?.superclass in [Object, ListNote])){
                parentClass = parentClass?.superclass
            }
            String className = parentClass?.simpleName
            className = className.replaceAll("(.)([A-Z])", '$1_$2')
            Connection connection = session.connection()
            int counter = 0;
            String code = ""
            String sequenceName = "seq_" + className.toLowerCase()
            PreparedStatement ps = connection.prepareStatement("SELECT nextval ('" + sequenceName + "') as nextval")
            ResultSet rs = ps.executeQuery()
            def requestAttributes = RequestContextHolder?.requestAttributesHolder?.get();

            //set firm code
            String firmCode = ""
            if (requestAttributes) {
                firmCode = PCPSessionUtils.getValue("firmCode")
            }else if (!firmCode && object?.hasProperty("firm")) {
                firmCode = object?.firm?.code
            }else if (!firmCode && object?.hasProperty("transientData") && object?.transientData?.firm) {
                firmCode = object?.transientData?.firm?.code
            }

            //set code generator
            if (object?.id != null) {
                code = "${firmCode}-" + (object?.id)
            } else {
                if (rs.next()) {
                    counter = rs.getLong("nextval")
                }
                code = "${firmCode}-" + (counter)
            }
            return code
        } catch (SQLException e) {
            println e.printStackTrace()
            throw new HibernateException("Unable to generate Stock Code Sequence")
        }

        return null
    }

}