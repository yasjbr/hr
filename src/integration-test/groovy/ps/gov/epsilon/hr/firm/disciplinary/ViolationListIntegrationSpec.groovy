package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.DomainInstanceBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for ViolationList service
 */
class ViolationListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = ViolationList
        service_domain = ViolationListService
        entity_name = "violationList"
        required_properties = PCPUtils.getRequiredFields(ViolationList)
        filtered_parameters = ["id"];
        primary_keys = ["encodedId", "id"]
        exclude_methods = ["delete", "save", "autocomplete", "search"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    /**
     * @goal test_searchWithRemotingValues method.
     * @expectedResult known total count.
     */
    def "test_searchWithRemotingValues"() {
        setup:
        println("*****************************test_searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        PagedList result = serviceInstance.searchWithRemotingValues(params)
        then:
        result.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property, result?.resultList[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test filter searchWithRemotingValues method with filter data.
     * @expectedResult known total count.
     */
    def "test_filter_searchWithRemotingValues"() {
        setup:
        println("*****************************test_filter_searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property,testInstance,map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        println "before test, params: ${params}"

        when:
        result = serviceInstance.searchWithRemotingValues(params)
        then:
        println ">>>" + result.totalCount + ",,,,,,,,,," + entity_total_count
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property, result?.resultList[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test customSearch method.
     * @expectedResult known total count.
     */
    def "test_customSearch"() {
        setup:
        println("*****************************test_customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        PagedList result = serviceInstance.customSearch(params)
        then:
        result.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property, result?.resultList[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance customSearch done with totalCount ${result.totalCount}")
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test filter customSearch method with filter data.
     * @expectedResult known total count.
     */
    def "test_filter_customSearch"() {
        setup:
        println("*****************************test_filter_customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property, testInstance, map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        result = serviceInstance.customSearch(params)
        then:
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property, result?.resultList[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test save method.
     * @expectedResult valid instance.
     */
    def "test_success_save"() {
        setup:
        println("*****************************test_success_save******************************************")
        def testInstance
        def instanceToSave = saveEntity(null, true)
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
            getPropertyValue(property, testInstance) != null
            getPropertyValue(property, testInstance) == getPropertyValue(property, map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test save success done with instance ${testInstance}")
    }

    /**
     * @goal test save method.
     * @expectedResult null instance and contains errors.
     */
    def "test_fail_save"() {
        setup:
        println("*****************************test_fail_save******************************************")
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
    def "test_success_sendList"() {
        setup:
        println("*****************************test_success_sendList******************************************")
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
            getPropertyValue(property, testInstance) != null
            getPropertyValue(property, testInstance) == getPropertyValue(property, map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test sendList success done with instance ${testInstance}")
    }

    /**
     * @goal test sendList method.
     * @expectedResult null instance and contains errors.
     */
    def "test_fail_sendList"() {
        setup:
        println("*****************************test_fail_sendList******************************************")
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


    @Override
    ViolationList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        ViolationList instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            tableData?.domain?.withTransaction { status ->
                Map addedMap = [:]
                if (tableData.isJoinTable) {
                    addedMap.putAll(props)
                }
                instance = tableData?.domain?.buildWithoutValidation(addedMap)

                //add details
                def violationListEmployee1 = ViolationListEmployee.buildWithoutSave()
                def violationListEmployee2 = ViolationListEmployee.buildWithoutSave()
                def violationListEmployee3 = ViolationListEmployee.buildWithoutSave()

                instance.addToViolationListEmployees(violationListEmployee1)
                instance.addToViolationListEmployees(violationListEmployee2)
                instance.addToViolationListEmployees(violationListEmployee3)
                boolean validated = instance.validate()
                if (!validated) {
                    DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                    if (!builder) {
                        builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                        builders.put(tableData?.objectName, builder)
                    }
                }
                once_save_properties.each { property ->
                    if (PCPSessionUtils.getValue(property)) {
                        instance."${property}" = PCPSessionUtils.getValue(property)
                    }
                }
                if (!discardSave) {
                    instance.save(flush: true, failOnError: true)
                }

                //set current status
                def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
                instance.currentStatus = currentStatus

                if (!discardSave) {
                    instance.save(flush: true, failOnError: true)
                }
                once_save_properties.each { property ->
                    if (!PCPSessionUtils.getValue(property)) {
                        PCPSessionUtils.setValue(property, instance."${property}")
                    }
                }
                status.setRollbackOnly()
            }
        }
        return instance
    }
}