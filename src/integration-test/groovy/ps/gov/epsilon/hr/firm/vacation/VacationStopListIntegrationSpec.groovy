package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationStopList service
 */
class VacationStopListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationStopList
        service_domain =  VacationStopListService
        entity_name = "vacationStopList"
        required_properties = PCPUtils.getRequiredFields( VacationStopList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}