package ps.gov.epsilon.hr.firm.lookups

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
 * unit test for ProvinceLocation controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ProvinceLocation])
@Build([ProvinceLocation])
@TestFor(ProvinceLocationController)
class ProvinceLocationControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ProvinceLocation
        service_domain = ProvinceLocationService
        entity_name = "provinceLocation"
        required_properties = PCPUtils.getRequiredFields(ProvinceLocation)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}