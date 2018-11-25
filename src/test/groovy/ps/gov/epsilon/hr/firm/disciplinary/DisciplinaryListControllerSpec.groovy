package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.handler.NullableConstraintHandler
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
import nl.flotsam.xeger.Xeger
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Assume
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

/**
 * unit test for DisciplinaryList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryList])
@Build([DisciplinaryList,DisciplinaryRecordJudgment,CorrespondenceListStatus])
@TestFor(DisciplinaryListController)
class DisciplinaryListControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = DisciplinaryList
        service_domain = DisciplinaryListService
        entity_name = "disciplinaryList"
        List required = PCPUtils.getRequiredFields(DisciplinaryList)
        required << "code"
        required_properties = required
        filtered_parameters = ["code"];
        primary_key_values = ["encodedId","id"]
        exclude_actions = ["create","edit","delete","save","update","autocomplete","list"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(DisciplinaryList?.class)
        }

    }

    def setup() {
        sharedService.grailsApplication = grailsApplication
    }


    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.DISCIPLINARY_LIST
        model.referenceObject == DisciplinaryList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageDisciplinaryList action.
     * @expectedResult response with known model.
     */
    def "test_manageDisciplinaryList"() {

        setup:
        println("************************test_manageDisciplinaryList********************************")
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
        controller.manageDisciplinaryList()

        then:
        required_properties.each { String property ->
            model."${entity_name}"."${property}" == getPropertyValue(property,testInstance)
        }
        response.status != HttpStatus.NOT_FOUND.value()
        println("test_show done with model: ${model."${entity_name}"}")
    }


    /**
     * @goal test manageDisciplinaryList action.
     * @expectedResult response with status 404.
     */
    def "test_manageDisciplinaryList_not_found"() {

        setup:
        println("************************test_manageDisciplinaryList_not_found********************************")
        when:
        controller.manageDisciplinaryList()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test_show done with satus: ${response.status}")
    }


    /**
     * @goal test sendDataModal action.
     * @expectedResult response with known model.
     */
    def "test_sendDataModal"() {

        setup:
        println("************************test_sendDataModal********************************")
        DisciplinaryList disciplinaryList = saveEntity()
        when:
        controller.params["id"] = disciplinaryList?.encodedId
        controller.sendDataModal()

        then:
        model != null
        model != [:]
        model.disciplinaryList.id == disciplinaryList?.id
        println("test showDetails done with model: ${model}")
    }

    /**
     * @goal test sendDataModal action.
     * @expectedResult response with known model.
     */
    def "test_sendDataModal_no_data"() {

        setup:
        println("************************test_sendDataModal_no_data********************************")
        when:
        controller.params["id"] = null
        controller.sendDataModal()

        then:
        response.text == ""
        println("test sendDataModal no data done with model: ${model}")
    }


    /**
     * @goal test sendData action with ajax request.
     * @expectedResult response with known model with errors.
     */
    def "test_failed_sendData"() {

        setup:
        println("************************test_failed_sendData********************************")

        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.sendData()

        then:
        response.json.success == false
        response.json.message.contains(validationTagLib.message(code: "default.not.found.message"))
        println("test sendData ajax failed and new count is ${response.json}")
    }

    /**
     * @goal test sendData action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_sendData"() {

        setup:
        println("************************test_success_sendData********************************")
        DisciplinaryList disciplinaryList = DisciplinaryList.build()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.params["encodedId"] = disciplinaryList?.encodedId
        controller.params["fromDate"] = "20/10/2017"
        controller.params["manualOutgoingNo"] = "004499"

        controller.sendData()

        then:
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "list.sent.message"))).toString()
        response.json.data.manualOutgoingNo == "004499"
        response.json.data.errors == null
        println("test sendData ajax success and new count is ${response.json}")
    }


    @Override
    public DisciplinaryList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        DisciplinaryList instance
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
            def disciplinaryRecordJudgment1 = DisciplinaryRecordJudgment.buildWithoutSave()
            def disciplinaryRecordJudgment2 = DisciplinaryRecordJudgment.buildWithoutSave()
            def disciplinaryRecordJudgment3 = DisciplinaryRecordJudgment.buildWithoutSave()

            instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment1)
            instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment2)
            instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment3)

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
    public DisciplinaryList saveEntity(TestDataObject tableData = null) {
        DisciplinaryList instance = fillEntity(tableData)
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