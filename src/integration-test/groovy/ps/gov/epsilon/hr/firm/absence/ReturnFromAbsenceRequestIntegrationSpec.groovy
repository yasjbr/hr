package ps.gov.epsilon.hr.firm.absence

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ReturnFromAbsenceRequest service
 */
class ReturnFromAbsenceRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ReturnFromAbsenceRequest
        service_domain =  ReturnFromAbsenceRequestService
        entity_name = "returnFromAbsenceRequest"
        required_properties = PCPUtils.getRequiredFields( ReturnFromAbsenceRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}