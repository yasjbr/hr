package ps.gov.epsilon.hr.firm.child

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
 * unit test for ChildListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ChildListEmployee])
@Build([ChildListEmployee])
@TestFor(ChildListEmployeeController)
class ChildListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ChildListEmployee
        service_domain = ChildListEmployeeService
        entity_name = "childListEmployee"
        required_properties = PCPUtils.getRequiredFields(ChildListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}