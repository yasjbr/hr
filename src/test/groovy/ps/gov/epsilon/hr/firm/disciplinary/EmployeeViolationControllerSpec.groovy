package ps.gov.epsilon.hr.firm.disciplinary

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
 * unit test for EmployeeViolation controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([EmployeeViolation])
@Build([EmployeeViolation])
@TestFor(EmployeeViolationController)
class EmployeeViolationControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = EmployeeViolation
        service_domain = EmployeeViolationService
        entity_name = "employeeViolation"
        required_properties = PCPUtils.getRequiredFields(EmployeeViolation)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}