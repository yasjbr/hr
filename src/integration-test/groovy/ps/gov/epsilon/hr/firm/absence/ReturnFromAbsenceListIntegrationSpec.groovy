package ps.gov.epsilon.hr.firm.absence

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ReturnFromAbsenceList service
 */
class ReturnFromAbsenceListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ReturnFromAbsenceList
        service_domain =  ReturnFromAbsenceListService
        entity_name = "returnFromAbsenceList"
        required_properties = PCPUtils.getRequiredFields( ReturnFromAbsenceList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}