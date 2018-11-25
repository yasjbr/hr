package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for vacationRequest service
 */
class VacationRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacationRequest
        service_domain = VacationRequestService
        entity_name = "vacationRequest"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacationRequest)
        filtered_parameters = ["id"]
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_keys = ["id", "encodedId"]
        exclude_methods = ["delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    /**
     * @goal test delete method.
     * @expectedResult empty instance and new records count equals previous count.
     */
    def "new_test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        saveEntity()
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        searchMap.put("id", testInstance?.id)
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance?.trackingInfo?.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        deletedInstance != null
        newCount == previousCount
        println("test delete success and new count is ${newCount}")
    }

    /**
     * @goal test delete method.
     * @expectedResult not null instance and new records count equals previous count.
     */
    def "new_test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        DeleteBean deleteBean = new DeleteBean()
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        def newCount = domain_class.count()
        newCount == previousCount
        println("test_delete fail done")
    }

    /**
     * @goal test saveAll method.
     * @expectedResult vacationRequest
     */
    def "test_selectEmployee_success"() {
        setup:
        println("************************test_selectEmployee_success********************************")
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap["vacationType.id"] = 1L
        parameterMap["employeeId"] = 1L
        serviceInstance.selectEmployee(parameterMap)

        then:
        def vacationRequest = serviceInstance.selectEmployee(parameterMap)
        println("test select Employee success done with : ${vacationRequest}")
    }

    /**
     * @goal test saveAll method.
     * @expectedResult vacationRequest
     */
    def "test_selectEmployee_failed"() {
        setup:
        println("************************test_selectEmployee_failed********************************")
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap["vacationType.id"] = null
        parameterMap["employeeId"] = null
        serviceInstance.selectEmployee(parameterMap)

        then:
        def vacationRequest = serviceInstance.selectEmployee(parameterMap)
        println("test selectEmployee failed done with : ${vacationRequest}")
    }

    /**
     * @goal test saveAll method.
     * @expectedResult valid instance.
     */
    def "test success saveAll"() {
        setup:
        println("*****************************test success saveAll******************************************")
        def testInstance
        def instanceToSave = saveEntity(null, true)
        Map map = [:]
        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value != null) {
                map.put(property, value)
            } else {
                value = getPropertyValue(property, instanceToSave)
                if (value == null) {
                    value = entity_name + "_" + property + "_" + counter
                }
                map.put(property, value)
            }
        }
        map.put("employeeIdList", [1L, 2L, 3L, 4L, 5L])
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = saveEntity(object, true)
            params[object.paramName] = null
            object.requiredProperties.each { property ->
                params[object.paramName + "." + property] = getPropertyValue(property, objectParams)
            }
            objectParams = null
        }
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
        println("test saveAll success done with instance ${testInstance}")
    }

    /**
     * @goal test saveAll method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail saveAll"() {
        setup:
        println("*****************************test fail saveAll******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        include_save_properties.each { TestDataObject object ->
            params[object.paramName] = null
            object.requiredProperties.each { property ->
                params[object.paramName + "." + property] = null
            }
        }
        testInstance = serviceInstance.save(params)
        then:
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance save All fail with errors ${testInstance.errors.allErrors}")
    }

}