package ps.gov.epsilon.hr.firm.general

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
 * unit test for GeneralListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([GeneralListEmployee])
@Build([GeneralListEmployee])
@TestFor(GeneralListEmployeeController)
class GeneralListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = GeneralListEmployee
        service_domain = GeneralListEmployeeService
        entity_name = "generalListEmployee"
        required_properties = PCPUtils.getRequiredFields(GeneralListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}