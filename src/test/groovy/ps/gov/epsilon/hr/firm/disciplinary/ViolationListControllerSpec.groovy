package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsClass
import grails.core.GrailsApplication
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.absence.Absence
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

import java.time.ZonedDateTime

/**
 * unit test for ViolationList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ViolationList])
@Build([ViolationList, JoinedFirmOperationDocument, ViolationListEmployee, Absence, CorrespondenceListStatus])
@TestFor(ViolationListController)
class ViolationListControllerSpec extends CommonUnitSpec {
    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)

    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = ViolationList
        service_domain = ViolationListService
        entity_name = "violationList"
        List required = PCPUtils.getRequiredFields(ViolationList)
        required << "code"
        required_properties = required
        filtered_parameters = ["code"];
        primary_key_values = ["encodedId","id"]
        exclude_actions = ["delete","autocomplete", "list"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(ViolationList?.class)
        }
    }

    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()
        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
    }

    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()
        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.ABSENCE
        model.referenceObject == ViolationList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageViolationList action.
     * @expectedResult response with known model.
     */
    def "test_manageViolationList"() {
        setup:
        println("************************test_manageViolationList********************************")
        def testInstance = saveEntity()
        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,testInstance,true)
        }
        required_properties.each { String property ->
            def value = getPropertyValue(property,testInstance)
            if(value instanceof String){
                controller.params["${property}"] = value
            }
        }
        controller.manageViolationList()
        then:
        required_properties.each { String property ->
            model."${entity_name}"."${property}" == getPropertyValue(property,testInstance)
        }
        response.status != HttpStatus.NOT_FOUND.value()
        println("test_show done with model: ${model."${entity_name}"}")
    }


    /**
     * @goal test manageViolationList action.
     * @expectedResult response with status 404.
     */
    def "test_manageViolationList_not_found"() {
        setup:
        println("************************test_manageViolationList_not_found********************************")
        when:
        controller.manageViolationList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test_show done with satus: ${response.status}")
    }


    /**
     * @goal test sendListModal action.
     * @expectedResult response with known model.
     */
    def "test_sendListModal"() {
        setup:
        println("************************test_sendListModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.SUBMITTED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.sendListModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_sendListModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test sendListModal action.
     * @expectedResult response with known model.
     */
    def "test_sendListModal_no_data"() {

        setup:
        println("************************test_sendListModal_no_data********************************")
        when:
        controller.params["id"] = null
        controller.sendListModal()

        then:
        response.text == ""
        println("test sendListModal no data done with model: ${model}")
    }

    /**
     * @goal test sendList action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_sendList"() {

        setup:
        println("************************test_override_success_sendList********************************")
        ViolationList violationList = ViolationList.build()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.params["encodedId"] = violationList?.encodedId
        controller.params["fromDate"] = "20/10/2017"
        controller.params["manualOutgoingNo"] = "004499"

        controller.sendList()

        then:
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "list.sent.message"))).toString()
        response.json.data.manualOutgoingNo == "004499"
        response.json.data.errors == null
        println("test sendList ajax success and new count is ${response.json}")
    }


    @Override
    public ViolationList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        ViolationList instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            Map addedMap = [:]
            if (tableData.hasSecurity) {
                addedMap.put("springSecurityService", springSecurityService)
            }
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
            if(!validated) {
                DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                if (!builder) {
                    builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                    builders.put(tableData?.objectName, builder)
                }
            }
        }
        return instance
    }


    @Override
    public ViolationList saveEntity(TestDataObject tableData = null) {
        ViolationList instance = fillEntity(tableData)
        once_save_properties.each {property->
            if(PCPSessionUtils.getValue(property)){
                instance."${property}" = PCPSessionUtils.getValue(property)
            }
        }
        instance.save(flush: true, failOnError: true)
        //set current status
        def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
        instance.currentStatus = currentStatus
        instance.save(flush: true, failOnError: true)
        once_save_properties.each {property->
            if(!PCPSessionUtils.getValue(property)){
                PCPSessionUtils.setValue(property,instance."${property}")
            }
        }
        return instance
    }



}