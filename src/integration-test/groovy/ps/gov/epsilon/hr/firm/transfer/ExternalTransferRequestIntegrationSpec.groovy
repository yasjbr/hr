package ps.gov.epsilon.hr.firm.transfer

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for ExternalTransferRequest service
 */
class ExternalTransferRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = ExternalTransferRequest
        service_domain = ExternalTransferRequestService
        entity_name = "externalTransferRequest"
        required_properties = PCPUtils.getRequiredFields(ExternalTransferRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id", "firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
        is_remoting = true
    }


    /**
     * @goal test getPreCreateInstance method.
     * @expectedResult get not null instance.
     */
    def "test success getPreCreateInstance"() {
        setup:
        println("*****************************test success getPreCreateInstance******************************************")
        ExternalTransferRequest testInstance
        ExternalTransferRequest.withTransaction { status ->
            testInstance = saveEntity(null,true)
            testInstance.requestStatus = EnumRequestStatus.REJECTED
            testInstance.save(flush: true, failOnError: true)
            status.setRollbackOnly()
        }
        Map map = [:]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        params.put("employeeId",testInstance?.employee?.id)

        when:
        def instance = serviceInstance.getPreCreateInstance(params)
        then:
        instance != null
        instance.employee != null
        instance.hasErrors() == false

        println("test getPreCreateInstance instance success with data: ${instance}")
    }

    /**
     * @goal test getPreCreateInstance method.
     * @expectedResult getPreCreateInstance null instance.
     */
    def "test getPreCreateInstance already in progress"() {
        setup:
        println("*****************************getPreCreateInstance already in progress******************************************")
        Map map = [:]
        ExternalTransferRequest testInstance
        ExternalTransferRequest.withTransaction { status ->
            testInstance = saveEntity(null,true)
            testInstance.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            testInstance.save(flush: true, failOnError: true)
            status.setRollbackOnly()
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        params.put("employeeId",testInstance?.employee?.id)

        when:

        def instance = serviceInstance.getPreCreateInstance(params)

        then:

        instance != null
        instance.employee == null
        instance.hasErrors() == true

        println("test getPreCreateInstance already in progress")
    }

    /**
     * @goal test getPreCreateInstance method.
     * @expectedResult getPreCreateInstance null instance.
     */
    def "test getPreCreateInstance already approved"() {
        setup:
        println("*****************************getPreCreateInstance already approved******************************************")
        Map map = [:]
        ExternalTransferRequest testInstance
        ExternalTransferRequest.withTransaction { status ->
            testInstance = saveEntity(null,true)
            testInstance.requestStatus = EnumRequestStatus.APPROVED
            testInstance.save(flush: true, failOnError: true)
            status.setRollbackOnly()
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        params.put("employeeId",testInstance?.employee?.id)

        when:

        def instance = serviceInstance.getPreCreateInstance(params)

        then:

        instance != null
        instance.employee == null
        instance.hasErrors() == true

        println("test getPreCreateInstance already approved")
    }

    /**
     * @goal test getPreCreateInstance method.
     * @expectedResult getPreCreateInstance null instance.
     */
    def "test fail getPreCreateInstance"() {
        setup:
        println("*****************************test fail getPreCreateInstance******************************************")
        Map map = [:]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        def instance = serviceInstance.getPreCreateInstance(params)
        then:
        instance != null
        instance.employee == null
        println("test instance getPreCreateInstance fail done")
    }

    public Object saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {

        ExternalTransferRequest instanceToSave

        ExternalTransferRequest.withTransaction { status ->


            JobTitle jobTitle = JobTitle.build()
            MilitaryRank militaryRank = MilitaryRank.build()
            EmploymentCategory employmentCategory = EmploymentCategory.build()


            EmploymentRecord currentEmploymentRecord = EmploymentRecord.build(
                    fromDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                    jobTitle:jobTitle,
                    employmentCategory:employmentCategory,

            )

            EmployeePromotion currentEmployeeMilitaryRank = EmployeePromotion.build(
                    militaryRank: militaryRank,
                    dueReason: EnumPromotionReason.EXCEPTIONAL,
                    actualDueDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                    dueDate: PCPUtils.parseZonedDateTime("20/12/2016")
            )

            Employee employee = Employee.build(
                    currentEmploymentRecord:currentEmploymentRecord,
                    currentEmployeeMilitaryRank: currentEmployeeMilitaryRank
            )


            instanceToSave = ExternalTransferRequest.buildWithoutValidation(employee: employee)

            if (!discardSave) {
                instanceToSave.save(flush: true,failOnError:true)
            }

            status.setRollbackOnly()
        }

        return instanceToSave
    }
}