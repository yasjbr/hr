package ps.gov.epsilon.hr.firm.allowance

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for AllowanceListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([AllowanceListEmployee])
@Build([AllowanceListEmployee])
@TestFor(AllowanceListEmployeeController)
class AllowanceListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = AllowanceListEmployee
        service_domain = AllowanceListEmployeeService
        entity_name = "allowanceListEmployee"
        required_properties = PCPUtils.getRequiredFields(AllowanceListEmployee)
        filtered_parameters = ["id"]
        exclude_actions = ['autocomplete']
        primary_key_values = ["id", "encodedId"]
    }
}