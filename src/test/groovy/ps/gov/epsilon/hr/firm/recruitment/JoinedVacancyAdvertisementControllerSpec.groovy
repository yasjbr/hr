package ps.gov.epsilon.hr.firm.recruitment

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
 * unit test for JoinedVacancyAdvertisement controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([JoinedVacancyAdvertisement])
@Build([JoinedVacancyAdvertisement])
@TestFor(JoinedVacancyAdvertisementController)
class JoinedVacancyAdvertisementControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = JoinedVacancyAdvertisement
        service_domain = JoinedVacancyAdvertisementService
        entity_name = "joinedVacancyAdvertisement"
        required_properties = PCPUtils.getRequiredFields(JoinedVacancyAdvertisement)
        filtered_parameters = ["id"]
        autocomplete_property = "vacancy.job.descriptionInfo.localName"
        primary_key_values = ["id", "encodedId"]
    }
}