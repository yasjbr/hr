package ps.gov.epsilon.hr.firm.disciplinary

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryListJudgmentSetup
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for DisciplinaryRequest service
 */
class DisciplinaryRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  DisciplinaryRequest
        service_domain =  DisciplinaryRequestService
        entity_name = "disciplinaryRequest"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryRequest)
        filtered_parameters = ["id"];
        autocomplete_property = "id";
        primary_keys = ["encodedId"]
        exclude_methods = ["delete","save"]
        session_parameters = ["firm Id": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }

    /**
     * @goal test save method.
     * @expectedResult valid instance.
     */
    def "test override success save"() {
        setup:
        println("*****************************test success save******************************************")
        def testInstance
        def instanceToSave = saveEntity(null,true)

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee


        Map map = [:]
        required_properties.each { String property ->
            def value = getPropertyValue(property, instanceToSave)
            if (value == null) {
                value = entity_name + "_" + property + "_" + counter
            }
            map.put(property, value)
        }


        //add custom params
        map["disciplinaryCategory"] = disciplinaryCategory?.id
        map["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        map["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]
        map["firm.id"] = instanceToSave?.firm?.id

        //judgment 1 params
        map["value_${judgment1?.id}"] = "20"
        map["unitId_${judgment1?.id}"] = "11"
        map["note_${judgment1?.id}"] = "note"

        //judgment 2 params
        map["value_${judgment1?.id}"] = "20"
        map["unitId_${judgment1?.id}"] = "11"
        map["orderNo_${judgment1?.id}"] = "001122"

        //currencyJudgment params
        map["value_${currencyJudgment?.id}"] = "20"
        map["currencyId_${currencyJudgment?.id}"] = "4"

        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.save(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test save success done with instance ${testInstance}")
    }

    /**
     * @goal test save method.
     * @expectedResult null instance and contains errors.
     */
    def "test override fail save"() {
        setup:
        println("*****************************test fail save******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.save(params)
        then:
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance save fail with errors ${testInstance.errors.allErrors}")
    }



    /**
     * @goal test search method.
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



    /**
     * @goal test get method.
     * @expectedResult get not null instance.
     */
    def "test success getInstanceWithRemotingValues"() {
        setup:
        println("*****************************test success getInstanceWithRemotingValues******************************************")
        def testInstance = saveEntity()
        Map map = [:]
        filtered_parameters.each { property ->
            map.put(property,getPropertyValue(property,testInstance,true))
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }
        when:
        def instance
        if(hashing_entity && with_hashing_flag == true) {
            instance = serviceInstance.getInstance(params,true)
        }else{
            instance = serviceInstance.getInstance(params)
        }
        then:
        instance != null
        filtered_parameters.each { property ->
            getPropertyValue(property,instance) != null
        }
        println("test getInstanceWithRemotingValues instance success with data: ${instance}")
    }

    /**
     * @goal test getInstanceWithRemotingValues method.
     * @expectedResult getInstanceWithRemotingValues null instance.
     */
    def "test fail getInstanceWithRemotingValues"() {
        setup:
        println("*****************************test fail getInstanceWithRemotingValues******************************************")
        Map map = [:]
        filtered_parameters.each { property ->
            if(hashing_entity == property){
                map.put(property,null)
            }else{
                map.put(property,"NOT_FOUND")
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        def instance
        if(hashing_entity && with_hashing_flag == true) {
            instance = serviceInstance.getInstance(params,true)
        }else{
            instance = serviceInstance.getInstance(params)
        }
        then:
        instance == null
        println("test instance getInstanceWithRemotingValues fail done")
    }
}