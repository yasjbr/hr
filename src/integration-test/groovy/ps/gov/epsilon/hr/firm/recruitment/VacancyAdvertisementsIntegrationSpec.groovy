package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for VacancyAdvertisements service
 */
class VacancyAdvertisementsIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacancyAdvertisements
        service_domain = VacancyAdvertisementsService
        entity_name = "vacancyAdvertisements"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacancyAdvertisements)
        filtered_parameters = ["id"]
        autocomplete_property = "title"
        primary_keys = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete=true
        is_encrypted_delete=false

    }

    def "test_addVacancyToVacancyAdvertisements"() {
        setup:
        println("************************test_addVacancyToVacancyAdvertisements********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            parameterMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        parameterMap["vacancyAdvertisementsId"] = testInstance?.id
        parameterMap["id"] = testInstance?.id
        parameterMap["check_vacancyTable"] = testInstance?.joinedVacancyAdvertisement?.id
        PCPSessionUtils.setValue("firmId",firm.id)
        serviceInstance.addVacancyToVacancyAdvertisements(parameterMap)

        then:
        def vacancyList = serviceInstance.search(parameterMap)?.joinedVacancyAdvertisement
        vacancyList?.each { Vacancy vacancy ->
            vacancy?.vacancyStatus == EnumVacancyStatus.POSTED
            println("test_addVacancyToVacancyAdvertisements  done")
        }
    }

    def "test_deleteVacancyFromVacancyAdvertisements"() {
        setup:
        println("************************test_deleteVacancyFromVacancyAdvertisements********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            parameterMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        parameterMap["id"] = testInstance?.id
        PCPSessionUtils.setValue("firmId",firm.id)
        serviceInstance.deleteVacancyFromVacancyAdvertisements(parameterMap)

        then:
        def vacancyList = serviceInstance.search(parameterMap)?.joinedVacancyAdvertisement
        vacancyList?.each { Vacancy vacancy ->
            vacancy?.vacancyStatus == EnumVacancyStatus.NEW
            println("test_deleteVacancyFromVacancyAdvertisements  done")
        }
    }

}