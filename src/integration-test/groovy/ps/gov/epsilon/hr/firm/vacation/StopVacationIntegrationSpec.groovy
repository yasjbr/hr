package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for StopVacation service
 */
class StopVacationIntegrationSpec extends CommonIntegrationSpec {


    def setupSpec() {
        domain_class = StopVacationRequest
        service_domain = StopVacationRequestService
        entity_name = "stopVacationRequest"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(StopVacationRequest)
        filtered_parameters = ["id"]
        exclude_methods = ["autocomplete", "delete", "resultListToMap"]

    }
}