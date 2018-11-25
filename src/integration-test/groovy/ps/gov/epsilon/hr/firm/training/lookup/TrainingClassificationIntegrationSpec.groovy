package ps.gov.epsilon.hr.firm.training.lookup

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for TrainingClassification service
 */
class TrainingClassificationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = TrainingClassification
        service_domain = TrainingClassificationService
        entity_name = "trainingClassification"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(TrainingClassification)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", 'encodedId']
        is_virtual_delete=true
        is_encrypted_delete=false
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

}