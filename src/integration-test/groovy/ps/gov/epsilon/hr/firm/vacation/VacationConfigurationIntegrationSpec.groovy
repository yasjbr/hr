package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationConfiguration service
 */
class VacationConfigurationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationConfiguration
        service_domain =  VacationConfigurationService
        entity_name = "vacationConfiguration"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields( VacationConfiguration)
        filtered_parameters = ["id"]
        autocomplete_property = "vacationType.descriptionInfo.localName"
        exclude_methods = ['filter','autocomplete','search']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }


    def "new_test_filter_search"() {
        setup:
        println("*****************************test filter search******************************************")
        saveEntity()
        saveEntity()
        def testInstance = new VacationConfiguration(id: 1L, militaryRank: new MilitaryRank(id:1L,descriptionInfo: new DescriptionInfo(localName: "testObject")),vacationType: new VacationType(id:1L,descriptionInfo: new DescriptionInfo(localName: "testObject")), encodedId: "md==",vacationTransferValue: 1,allowedValue: 14, maxAllowedValue: 18,checkForAnnualLeave: true,isBreakable: true,isTransferableToNewYear: true)
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            if (is_join_table) {
                def key = join_table_ids.get(primary_keys) ?: "id"
                map[(property + ".${key}")] = testInstance?."${property}"?."${key}"
            } else {
                sendParams(property, testInstance, map)

            }
        }
        println "map:- $map"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result
        when:
        result = serviceInstance.search(params)
        then:
        result.totalCount == 2
        filtered_parameters.each { property ->
            getPropertyValue(property, result[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance search done with result ${result}")
    }
}