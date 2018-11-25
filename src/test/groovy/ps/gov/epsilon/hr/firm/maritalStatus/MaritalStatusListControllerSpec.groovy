package ps.gov.epsilon.hr.firm.maritalStatus

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
 * unit test for MaritalStatusList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([MaritalStatusList])
@Build([MaritalStatusList])
@TestFor(MaritalStatusListController)
class MaritalStatusListControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = MaritalStatusList
        service_domain = MaritalStatusListService
        entity_name = "maritalStatusList"
        required_properties = PCPUtils.getRequiredFields(MaritalStatusList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}