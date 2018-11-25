package ps.gov.epsilon.hr.firm.maritalStatus

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for MaritalStatusEmployeeNote service
 */
class MaritalStatusEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  MaritalStatusEmployeeNote
        service_domain =  MaritalStatusEmployeeNoteService
        entity_name = "maritalStatusEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( MaritalStatusEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}