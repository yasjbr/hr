package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for TrainingRejectionReason service
 */
class TrainingRejectionReasonIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  TrainingRejectionReason
        service_domain =  TrainingRejectionReasonService
        entity_name = "trainingRejectionReason"
        required_properties = PCPUtils.getRequiredFields( TrainingRejectionReason)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        is_encrypted_delete = false
        is_virtual_delete=true
        is_encrypted_delete=false
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}