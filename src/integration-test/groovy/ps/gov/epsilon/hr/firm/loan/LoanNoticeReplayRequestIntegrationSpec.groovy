package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.handler.NullableConstraintHandler
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import nl.flotsam.xeger.Xeger
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for LoanNoticeReplayRequest service
 */
class LoanNoticeReplayRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanNoticeReplayRequest
        service_domain = LoanNoticeReplayRequestService
        entity_name = "loanNoticeReplayRequest"
        required_properties = PCPUtils.getRequiredFields(LoanNoticeReplayRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        exclude_methods = ["list","save"]
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
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
        def value
        required_properties.each { String property ->
            value = getPropertyValue(property, instanceToSave)
            if (value == null) {
                value = entity_name + "_" + property + "_" + counter
            }
            map.put(property, value)
        }

        map.put("employee.id", getPropertyValue("employee.id", instanceToSave))
        map.put("loanNotice.encodedId", getPropertyValue("loanNotice.encodedId", instanceToSave))
        def firmId = getPropertyValue("firm.id", instanceToSave)
        map.put("firm.id",firmId )
        map.put("firmId", firmId)

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


    public Object saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if(!tableData){
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        def instance
        Map props = [:]
        List assignedProperties = []
        Object value
        tableData?.requiredProperties?.each { String property ->
            if (tableData && tableData?.data) {
                value = tableData.data.get(property)
                if (value instanceof TestDataObject) {
                    if(isEmbeddedClass(value?.domain) || tableData?.isJoinTable){
                        value = saveEntity(value)
                    }else{
                        value = saveEntity(value)?.id
                    }
                }
            }
            if(value == null){
                counter++;
                value = tableData?.domain?.simpleName + "_" + property + "_" + counter
            }else{
                assignedProperties << property
            }
            props.put(property, value)
            value = null
        }
        if(tableData?.disableSave){
            instance = tableData?.domain?.newInstance(props)
        }else{
            tableData?.domain?.withTransaction { status ->

                Map addedMap = [:]

                if (tableData.isJoinTable) {
                    addedMap.putAll(props)
                }

                JobTitle jobTitle = JobTitle.build()
                MilitaryRank militaryRank = MilitaryRank.build()
                EmploymentCategory employmentCategory = EmploymentCategory.build()
                LoanNotice loanNotice = LoanNotice.build()

                EmployeePromotion currentEmployeeMilitaryRank = EmployeePromotion.build(
                        militaryRank: militaryRank,
                        dueReason: EnumPromotionReason.EXCEPTIONAL,
                        actualDueDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                        dueDate: PCPUtils.parseZonedDateTime("20/12/2016")
                )

                EmploymentRecord currentEmploymentRecord = EmploymentRecord.build(
                        fromDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                        jobTitle:jobTitle,
                        employmentCategory:employmentCategory,

                )

                Employee employee = Employee.build(
                        currentEmploymentRecord:currentEmploymentRecord,
                        currentEmployeeMilitaryRank: currentEmployeeMilitaryRank
                )

                addedMap.putAll([loanNotice:loanNotice,requestStatus:  EnumRequestStatus.CREATED,currentEmploymentRecord:currentEmploymentRecord,employee: employee])

                instance = LoanNoticeReplayRequest.buildWithoutSave(addedMap)


                once_save_properties.each {property->
                    if(PCPSessionUtils.getValue(property)){
                        instance."${property}" = PCPSessionUtils.getValue(property)
                    }
                }

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