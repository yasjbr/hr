package ps.gov.epsilon.hr.firm.profile.lookups

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
 * unit test for EmployeeStatusCategory controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([EmployeeStatusCategory])
@Build([EmployeeStatusCategory])
@TestFor(EmployeeStatusCategoryController)
class EmployeeStatusCategoryControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = EmployeeStatusCategory
        service_domain = EmployeeStatusCategoryService
        entity_name = "employeeStatusCategory"
        required_properties = PCPUtils.getRequiredFields(EmployeeStatusCategory)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId":"firm.id"]
        once_save_properties = ["firm"]
    }

}