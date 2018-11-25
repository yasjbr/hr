package ps.gov.epsilon.hr.firm.correspondenceList.lookup

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
 * unit test for CorrespondenceTemplate controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([CorrespondenceTemplate])
@Build([CorrespondenceTemplate])
@TestFor(CorrespondenceTemplateController)
class CorrespondenceTemplateControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = CorrespondenceTemplate
        service_domain = CorrespondenceTemplateService
        entity_name = "correspondenceTemplate"
        required_properties = PCPUtils.getRequiredFields(CorrespondenceTemplate)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}