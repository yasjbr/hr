package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationExtensionList service
 */
class VacationExtensionListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationExtensionList
        service_domain =  VacationExtensionListService
        entity_name = "vacationExtensionList"
        required_properties = PCPUtils.getRequiredFields( VacationExtensionList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}