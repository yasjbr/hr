package ps.gov.epsilon.hr.firm.transfer

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ExternalTransferListEmployee service
 */
class ExternalTransferListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ExternalTransferListEmployee
        service_domain =  ExternalTransferListEmployeeService
        entity_name = "externalTransferListEmployee"

        required_properties = PCPUtils.getRequiredFields( ExternalTransferListEmployee)
        filtered_parameters = ["id"]

    }
}