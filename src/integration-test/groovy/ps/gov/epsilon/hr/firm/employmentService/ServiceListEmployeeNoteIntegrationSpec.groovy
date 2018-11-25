package ps.gov.epsilon.hr.firm.employmentService

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ServiceListEmployeeNote service
 */
class ServiceListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ServiceListEmployeeNote
        service_domain =  ServiceListEmployeeNoteService
        entity_name = "serviceListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( ServiceListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}