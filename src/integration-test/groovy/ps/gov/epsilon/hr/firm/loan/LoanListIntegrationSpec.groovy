package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.DomainInstanceBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for LoanList service
 */
class LoanListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanList
        service_domain = LoanListService
        entity_name = "loanList"
        List required = PCPUtils.getRequiredFields(LoanList)
        required << "code"
        required_properties = required
        filtered_parameters = ["code"]
        autocomplete_property = "name"
        exclude_methods = ["autocomplete"]
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
        is_remoting = true
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

        when:
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        testInstance = serviceInstance.sendList(params)

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

        when:
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        testInstance = serviceInstance.receiveList(params)

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
     * @goal test closeList method.
     * @expectedResult valid instance.
     */
    def "test success closeList"() {
        setup:
        println("*****************************test success closeList******************************************")
        def testInstance
        def instanceToSave = saveEntity()

        when:
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())


        testInstance = serviceInstance.closeList(params)



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
     * @goal test closeList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail closeList"() {
        setup:
        println("*****************************test fail closeList******************************************")
        Map map = [:]
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        map = serviceInstance.closeList(params)
        then:
        map.success == false
        map.errors.size() > 0
        println("test fail_closeList fail with errors ${map?.errors}")
    }


    /**
     * @goal test addRequest method.
     * @expectedResult Map.
     */
    def "test success addRequest"() {
        setup:
        println("*****************************test success addRequest******************************************")
        Map testInstance
        def instanceToSave = saveEntity()
        LoanRequest loanRequest = LoanRequest.build(firm: instanceToSave?.firm)

        when:
        Map map = [:]
        map["loanListId"] = instanceToSave?.id
        map["checked_requestIdsList"] = [loanRequest?.id]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())


        testInstance = serviceInstance.addRequest(params)



        then:
        testInstance != null
        testInstance.success == true
        testInstance.errors == []
        println("test success addRequest done with instance ${testInstance}")
    }


    /**
     * @goal test addRequest method.
     * @expectedResult Map.
     */
    def "test fail addRequest"() {
        setup:
        println("*****************************test fail addRequest******************************************")
        when:
        Map map = [:]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        Map testInstance = serviceInstance.addRequest(params)

        then:
        testInstance != null
        testInstance.success == false
        testInstance.errors.size() > 0
        println("test fail addRequest done with instance ${testInstance}")
    }

    /**
     * @goal test approveRequest method.
     * @expectedResult Map.
     */
    def "test success approveRequest"() {
        setup:
        println("*****************************test success approveRequest******************************************")
        Map testInstance
        Firm firm = Firm.build()
        LoanRequest loanRequest = LoanRequest.build(firm:firm)
        LoanListPerson loanListPerson = LoanListPerson.build(loanRequest:loanRequest,firm:firm)

        when:
        Map map = [:]
        PCPSessionUtils.setValue("firmId",firm?.id)
        map['note'] = "note"
        map['noteDate'] = "16/12/2017"
        map['effectiveDate'] = "16/12/2017"
        map['checked_loanPersonIdsList'] = [loanListPerson?.id]
        map['receivedPersonId'] = [1750L,1752L]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())


        testInstance = serviceInstance.approveRequest(params)



        then:
        testInstance != null
        testInstance.success == true
        testInstance.errors == []
        println("test success approveRequest done with instance ${testInstance}")
    }


    /**
     * @goal test approveRequest method.
     * @expectedResult Map.
     */
    def "test fail approveRequest"() {
        setup:
        println("*****************************test fail approveRequest******************************************")
        Map testInstance

        when:
        Map map = [:]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        testInstance = serviceInstance.approveRequest(params)

        then:
        testInstance != null
        testInstance.success == false
        testInstance.errors.size() > 0
        println("test fail approveRequest done with instance ${testInstance}")
    }

    /**
     * @goal test rejectRequest method.
     * @expectedResult Map.
     */
    def "test success rejectRequest"() {
        setup:
        println("*****************************test success rejectRequest******************************************")
        Map testInstance
        LoanRequest loanRequest = LoanRequest.build()
        LoanListPerson loanListPerson = LoanListPerson.build(loanRequest:loanRequest)

        when:
        Map map = [:]
        map['note'] = "note"
        map['noteDate'] = "16/12/2017"
        map['checked_loanPersonIdsList'] = [loanListPerson?.id]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())


        testInstance = serviceInstance.rejectRequest(params)



        then:
        testInstance != null
        testInstance.success == true
        testInstance.errors == []
        println("test success rejectRequest done with instance ${testInstance}")
    }


    /**
     * @goal test rejectRequest method.
     * @expectedResult Map.
     */
    def "test fail rejectRequest"() {
        setup:
        println("*****************************test fail rejectRequest******************************************")
        Map testInstance
        when:
        Map map = [:]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        testInstance = serviceInstance.rejectRequest(params)

        then:
        testInstance != null
        testInstance.success == false
        testInstance.errors.size() > 0
        println("test fail rejectRequest done with instance ${testInstance}")
    }

    /**
     * @goal test customSearch method.
     * @expectedResult known total count.
     */
    def "test customSearch"() {
        setup:
        println("*****************************test customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        PagedList result = serviceInstance.customSearch(params)
        then:
        result.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance customSearch done with totalCount ${result.totalCount}")
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test filter customSearch method with filter data.
     * @expectedResult known total count.
     */
    def "test filter customSearch"() {
        setup:
        println("*****************************test filter customSearch******************************************")
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
        result = serviceInstance.customSearch(params)
        then:
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance customSearch done with result ${result}")
    }




    @Override
    LoanList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if(!tableData){
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        LoanList instance
        Map props = [:]
        if(tableData?.disableSave){
            instance = tableData?.domain?.newInstance(props)
        }else{
            LoanList.withTransaction { status ->
                Map addedMap = [:]
                if (tableData.isJoinTable) {
                    addedMap.putAll(props)
                }
                instance = tableData?.domain?.buildWithoutValidation(addedMap)

                boolean validated = instance.validate()
                if(!validated) {
                    DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                    if (!builder) {
                        builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                        builders.put(tableData?.objectName, builder)
                    }
                }

                once_save_properties.each {property->
                    if(PCPSessionUtils.getValue(property)){
                        instance."${property}" = PCPSessionUtils.getValue(property)
                    }
                }

                if (!discardSave) {
                    instance.save(flush: true,failOnError:true)
                }

                //set current status
                def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
                instance.currentStatus = currentStatus

                if (!discardSave) {
                    instance.save(flush: true,failOnError:true)
                }

                once_save_properties.each {property->
                    if(!PCPSessionUtils.getValue(property)){
                        PCPSessionUtils.setValue(property,instance."${property}")
                    }
                }

                status.setRollbackOnly()
            }
        }
        return instance
    }

}