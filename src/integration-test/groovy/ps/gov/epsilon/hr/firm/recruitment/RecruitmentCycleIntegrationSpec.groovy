package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumDepartmentType
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for RecruitmentCycle service
 */
class RecruitmentCycleIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = RecruitmentCycle
        service_domain = RecruitmentCycleService
        entity_name = "recruitmentCycle"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(RecruitmentCycle)
        filtered_parameters = ["id"]
        autocomplete_property = "name"
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
    /**
     * @goal test delete method.
     * @thenedResult request without params and response with success delete result.
     */
    def "new_test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        PCPSessionUtils.setValue("firmId", firm.id)
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        if (deletedInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance?.trackingInfo?.status == GeneralStatus.DELETED
        } else {
            deletedInstance?.trackingInfo?.status == GeneralStatus.ACTIVE
        }
        def newCount = domain_class.count()
        deletedInstance != null
        newCount == previousCount
        println("test delete success and new count is ${newCount}")
    }

    /**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with success delete result.
     */

    def "new_test_success_delete_ajax"() {

        setup:
        println("************************test_success_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        PCPSessionUtils.setValue("firmId", firm.id)
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        if (deletedInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance?.trackingInfo.status == GeneralStatus.DELETED
        } else {
            deletedInstance?.trackingInfo.status == GeneralStatus.ACTIVE
        }
        def newCount = domain_class.count()
        newCount == previousCount
        println("test delete ajax success and new count is ${newCount}")
    }

    /**
     * @goal test delete method.
     * @thenedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        PCPSessionUtils.setValue("firmId", firm.id)
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        if (deletedInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance?.trackingInfo.status == GeneralStatus.DELETED
        } else {
            deletedInstance?.trackingInfo.status == GeneralStatus.ACTIVE
        }
        def newCount = domain_class.count()
        newCount == previousCount
        println("test_delete fail done")
    }

    /**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete_ajax"() {

        setup:
        println("************************test_fail_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        PCPSessionUtils.setValue("firmId", firm.id)
        serviceInstance.delete(deleteBean, false)

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        if (deletedInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance?.trackingInfo.status == GeneralStatus.DELETED
        } else {
            deletedInstance?.trackingInfo.status == GeneralStatus.ACTIVE
        }
        def newCount = domain_class.count()
        newCount == previousCount
        println("test_delete ajax fail done")
    }


    def "test_getNextPhase"() {
        setup:
        println "*************test_getNextPhase**************"
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)

        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.id = testInstance?.id
        PCPSessionUtils.setValue("firmId", firm.id)
        serviceInstance.getNextPhase(parameterMap)

        then:
        def map = serviceInstance.getNextPhase(parameterMap)
        map != [:]
        map.errorType != null
        println "test_getNextPhase done with data : ${map}"
    }

    def "test_manageDepartmentData"() {
        setup:
        println "*************test_manageDepartmentData**************"
        def testInstance = saveEntity()

        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.id = testInstance?.id
        serviceInstance.manageDepartmentData(parameterMap)

        then:
        def map = serviceInstance.manageDepartmentData(parameterMap)
        map != [:]
        map.errorType != null
        println "test_manageDepartmentData done with data : ${map}"
    }

    def "test_addRecruitmentCycleToJobRequisition"() {
        setup:
        println "*************test_addRecruitmentCycleToJobRequisition**************"
        def testInstance = saveEntity()

        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.recruitmentCycleId = testInstance?.id
        serviceInstance.addRecruitmentCycleToJobRequisition(parameterMap)

        then:
        def status = serviceInstance.addRecruitmentCycleToJobRequisition(parameterMap)
        status != null
        println "test_addRecruitmentCycleToJobRequisition done with data : ${status}"
    }


}