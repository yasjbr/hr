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
 * unit test for ChildRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ChildRequest])
@Build([ChildRequest])
@TestFor(ChildRequestController)
class ChildRequestControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ChildRequest
        service_domain = ChildRequestService
        entity_name = "childRequest"
        required_properties = PCPUtils.getRequiredFields(ChildRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}