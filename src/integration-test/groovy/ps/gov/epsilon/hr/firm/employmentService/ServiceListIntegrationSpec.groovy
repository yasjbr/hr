package ps.gov.epsilon.hr.firm.employmentService

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for ServiceList service
 */
class ServiceListIntegrationSpec extends CommonIntegrationSpec {

    def setupSpec(){
        domain_class =  ServiceList
        service_domain =  ServiceListService
        entity_name = "serviceList"
        required_properties = PCPUtils.getRequiredFields( ServiceList)
        hashing_entity = "id"
        with_hashing_flag = false
        filtered_parameters = ["id"];
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["delete", "autocomplete"]
    }


    /**
     * @goal test searchWithRemotingValues method.
     * @expectedResult known total count.
     */
    def "test searchWithRemotingValues1"() {
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
//        result?.totalCount == (entity_total_count + 3)
//        filtered_parameters.each { property ->
//            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
//        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test searchWithRemotingValues method with filter data.
     * @expectedResult known total count.
     */
    def "test filter searchWithRemotingValues1"() {
        setup:
        println("*****************************test filter searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property,testInstance,map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        result = serviceInstance.searchWithRemotingValues(params)
        then:
//        result.totalCount == 1
//        filtered_parameters.each { property ->
//            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
//        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test save method.
     * @expectedResult valid instance.
     */
    def "test success save"() {
        setup:
        println("*****************************test success save******************************************")
        def testInstance
        def instanceToSave = saveEntity(null,true)
        Map map = [:]
        required_properties.each { String property ->
            def value = getPropertyValue(property, instanceToSave)
            if (value == null) {
                value = entity_name + "_" + property + "_" + counter
            }
            map.put(property, value)
        }
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
    def "test fail save"() {
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
     * @goal test sendList method.
     * @expectedResult valid instance.
     */
    def "test success sendList"() {
        setup:
        println("*****************************test success sendList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.sendList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test sendList success done with instance ${testInstance}")
    }

    /**
     * @goal test sendList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail sendList"() {
        setup:
        println("*****************************test fail sendList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.sendList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance sendList fail with errors ${testInstance.errors.allErrors}")
    }



    /**
     * @goal test receiveList method.
     * @expectedResult valid instance.
     */
    def "test success receiveList"() {
        setup:
        println("*****************************test success receiveList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.receiveList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test receiveList success done with instance ${testInstance}")
    }

    /**
     * @goal test receiveList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail receiveList"() {
        setup:
        println("*****************************test fail receiveList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.receiveList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance receiveList fail with errors ${testInstance.errors.allErrors}")
    }

    /**
     * @goal test receiveList method.
     * @expectedResult valid instance.
     */
    def "test success closeList"() {
        setup:
        println("*****************************test success closeList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "25/12/2017"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.closeList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test closeList success done with instance ${testInstance}")
    }

    /**
     * @goal test receiveList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail closeList"() {
        setup:
        println("*****************************test fail closeList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.closeList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance closeList fail with errors ${testInstance.errors.allErrors}")
    }


    /**
     * @goal test addEmploymentServiceRequestToList method.
     * @expectedResult valid instance.
     */
    def "test success addEmploymentServiceRequestToList"() {
        setup:
        println("*****************************test success addEmploymentServiceRequestToList******************************************")
        ServiceList serviceList
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)


        EmploymentServiceRequest request = EmploymentServiceRequest.build(
                employee:employee,
                requestType: EnumRequestType.END_OF_SERVICE,
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_requestIdsList"] = [request?.id]
        params["serviceListId"] = instanceToSave?.id
        params["id"] = instanceToSave?.id

        when:
        domain_class.withTransaction { status ->
            serviceList = serviceInstance.addEmploymentServiceRequestToList(params)
            status.setRollbackOnly()
        }
        then:
        !serviceList.hasErrors()
        request.requestStatus == EnumRequestStatus.ADD_TO_LIST
        !request.hasErrors()
        println("test addEmploymentServiceRequestToList success done with instance ${request}")
    }

    /**
     * @goal test addEmploymentServiceRequestToList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail addEmploymentServiceRequestToList"() {
        setup:
        println("*****************************test fail addEmploymentServiceRequestToList******************************************")
        ServiceList serviceList
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        MilitaryRankType militaryRankType = MilitaryRankType.build()

        EmploymentServiceRequest request = EmploymentServiceRequest.build(
                employee:employee,
                requestType: EnumRequestType.END_OF_SERVICE
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["serviceListId"] = instanceToSave?.id

        when:
        domain_class.withTransaction { status ->
            serviceList = serviceInstance.addEmploymentServiceRequestToList(params)
            status.setRollbackOnly()
        }
        then:
        serviceList.hasErrors()
        println("test instance addEmploymentServiceRequestToList fail with result ${serviceList}")
    }


    /**
     * @goal test addExceptionalToList method.
     * @expectedResult valid instance.
     */
    def "test success addExceptionalToList"() {
        setup:
        println("*****************************test success addExceptionalToList******************************************")
        ServiceList serviceList
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)


        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_employeeIdsList"] = [employee?.id]
        params["serviceListId"] = instanceToSave?.id
        params["id"] = instanceToSave?.id
        params["eligible"] = false

        when:
        domain_class.withTransaction { status ->
            serviceList = serviceInstance.addExceptionalToList(params)
            status.setRollbackOnly()
        }
        then:
        !serviceList.hasErrors()
        !employee.hasErrors()
        println("test addExceptionalToList success done with instance ${serviceList}")
    }

    /**
     * @goal test addExceptionalToList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail addExceptionalToList"() {
        setup:
        println("*****************************test fail addExceptionalToList******************************************")
        ServiceList serviceList
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["serviceListId"] = instanceToSave?.id

        when:
        domain_class.withTransaction { status ->
            serviceList = serviceInstance.addExceptionalToList(params)
            status.setRollbackOnly()
        }
        then:
        serviceList.hasErrors()
        println("test instance addExceptionalToList fail with result ${serviceList}")
    }




    /**
     * @goal test changeRequestToApproved method.
     * @expectedResult valid instance.
     */
    def "test success changeRequestToApproved"() {
        setup:
        println("*****************************test success changeRequestToApproved******************************************")
        def instanceToSave = saveEntity()

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        ServiceListEmployee serviceListEmployee = ServiceListEmployee.build(
                currentEmploymentRecord:employmentRecord,
                currentEmployeeMilitaryRank:employeePromotion,
                recordStatus: EnumListRecordStatus.NEW,
                employee: employee,
                serviceList:instanceToSave
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_serviceListEmployeeIdsList"] = [serviceListEmployee?.id]
        params["serviceList.id"] = instanceToSave.id
        params["expectedDateEffective"] = "22/09/2017"
        params["orderDate"] = "22/09/2017"
        params["orderNo"] = "80aa"
        params["id"] = instanceToSave?.id
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeRequestToApproved(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == true
        map.errors.size() == 0
        println("test instance changeRequestToApproved success is done with errors${map.errors}")
    }

    /**
     * @goal test changeRequestToApproved method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail changeRequestToApproved"() {
        setup:
        println("*****************************test fail changeRequestToApproved******************************************")
        def map
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        map = serviceInstance.changeRequestToApproved(params)
        then:
        map.saved == false
        map.errors.size() > 0
        println("test instance changeRequestToApproved fail with errors ${map.errors}")
    }



    /**
     * @goal test changeRequestToRejected method.
     * @expectedResult valid instance.
     */
    def "test success changeRequestToRejected"() {
        setup:
        println("*****************************test success changeRequestToRejected******************************************")
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        ServiceListEmployee serviceListEmployee = ServiceListEmployee.build(
                currentEmploymentRecord:employmentRecord,
                currentEmployeeMilitaryRank:employeePromotion,
                recordStatus: EnumListRecordStatus.NEW,
                employee: employee,
                serviceList:instanceToSave
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_serviceListEmployeeIdsList"] = [serviceListEmployee?.id]
        params["serviceList.id"] = instanceToSave.id
        params["id"] = instanceToSave?.id
        params["orderNo"] = "369"
        params["noteDate"]= "22/09/2017"
        params["note"] = "reject note"
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeRequestToRejected(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == true
        map.errors.size() == 0
        println("test instance changeRequestToRejected success is done with errors${map.errors}")
    }

    /**
     * @goal test changeRequestToRejected method.
     * @expectedResult valid instance.
     */
    def "test fail changeRequestToRejected with no note"() {
        setup:
        println("*****************************test fail changeRequestToRejected with no note******************************************")
        def instanceToSave = saveEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        ServiceListEmployee serviceListEmployee = ServiceListEmployee.build(
                currentEmploymentRecord:employmentRecord,
                currentEmployeeMilitaryRank:employeePromotion,
                recordStatus: EnumListRecordStatus.NEW,
                employee: employee,
                serviceList:instanceToSave
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_serviceListEmployeeIdsList"] = [serviceListEmployee?.id]
        params["serviceList.id"] = instanceToSave.id
        params["id"] = instanceToSave?.id
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeRequestToRejected(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == false
        map.errors.size() > 0
        println("test changeRequestToRejected failed with no note, with errors${map.errors}")
    }

    /**
     * @goal test changeRequestToRejected method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail changeRequestToRejected"() {
        setup:
        println("*****************************test fail changeRequestToRejected******************************************")
        def map
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        map = serviceInstance.changeRequestToRejected(params)
        then:
        map.saved == false
        map.errors.size() > 0
        println("test instance changeRequestToRejected fail with errors ${map.errors}")
    }

}