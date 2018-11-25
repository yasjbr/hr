package ps.gov.epsilon.hr.firm.allowance

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for AllowanceListEmployee service
 */
class AllowanceListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = AllowanceListEmployee
        service_domain = AllowanceListEmployeeService
        entity_name = "allowanceListEmployee"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(AllowanceListEmployee)
        filtered_parameters = ["id"];
        autocomplete_property = "allowanceRequest.employee.transientData.personDTO.localFullName"
        primary_keys = ["id", 'encodedId']

    }
}