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
 * unit test for PetitionRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([PetitionRequest])
@Build([PetitionRequest])
@TestFor(PetitionRequestController)
class PetitionRequestControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = PetitionRequest
        service_domain = PetitionRequestService
        entity_name = "petitionRequest"
        required_properties = PCPUtils.getRequiredFields(PetitionRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}