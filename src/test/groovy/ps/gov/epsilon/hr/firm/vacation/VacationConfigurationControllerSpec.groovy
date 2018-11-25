package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.junit.Assume
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.AttendanceType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationTypeService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for VacationConfiguration controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([VacationConfiguration])
@Build([VacationConfiguration, Firm])
@TestFor(VacationConfigurationController)
class VacationConfigurationControllerSpec extends CommonUnitSpec {
    VacationTypeService vacationTypeService = mockService(VacationTypeService)
    MilitaryRankService militaryRankService = mockService(MilitaryRankService)


    def setupSpec() {
        domain_class = VacationConfiguration
        service_domain = VacationConfigurationService
        entity_name = "vacationConfiguration"
        required_properties = PCPUtils.getRequiredFields(VacationConfiguration)
        filtered_parameters = ["id"]
        autocomplete_property = "vacationType.descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

}