package ps.gov.epsilon.hr.firm.disciplinary

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for DisciplinaryList service
 */
class DisciplinaryRecordJudgmentIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  DisciplinaryRecordJudgment
        service_domain =  DisciplinaryRecordJudgmentService
        entity_name = "disciplinaryRecordJudgment"
        List requiredList = PCPUtils.getRequiredFields( DisciplinaryRecordJudgment)
        requiredList << "value"
        required_properties = requiredList
        filtered_parameters = ["value"];
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["autocomplete","get","delete","save"]
    }


    /**
     * @goal test searchWithRemotingValues method.
     * @expectedResult known total count.
     */
    def "test searchWithRemotingValues"() {
        setup:
        println("*****************************test searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        def result = serviceInstance.searchWithRemotingValues(params)
        then:
        result?.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test searchWithRemotingValues method with filter data.
     * @expectedResult known total count.
     */
    def "test filter searchWithRemotingValues"() {
        setup:
        println("*****************************test filter searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            if(is_join_table){
                def key = join_table_ids.get(primary_keys)?:"id"
                map[(property + ".${key}")] = testInstance?."${property}"?."${key}"
            }else {
                sendParams(property,testInstance,map)
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        result = serviceInstance.searchWithRemotingValues(params)
        then:
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }
}