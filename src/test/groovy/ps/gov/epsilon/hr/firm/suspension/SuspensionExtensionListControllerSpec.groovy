package ps.gov.epsilon.hr.firm.suspension

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
 * unit test for SuspensionExtensionList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionExtensionList])
@Build([SuspensionExtensionList])
@TestFor(SuspensionExtensionListController)
class SuspensionExtensionListControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = SuspensionExtensionList
        service_domain = SuspensionExtensionListService
        entity_name = "suspensionExtensionList"
        required_properties = PCPUtils.getRequiredFields(SuspensionExtensionList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}