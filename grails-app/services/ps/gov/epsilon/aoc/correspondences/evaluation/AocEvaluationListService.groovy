package ps.gov.epsilon.aoc.correspondences.evaluation

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.AocListRecordService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.evaluation.EvaluationListService
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationItem
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationTemplate
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.PagedList

@Transactional
class AocEvaluationListService implements ICorrespondenceListService{

    MessageSource messageSource
    EvaluationListService evaluationListService
    AocListRecordService aocListRecordService

    @Override
    List<String> getDomainColumns() {
        return evaluationListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        // evaluationListService dosent has searchWithRemotingValues method
        return evaluationListService.search(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return evaluationListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return evaluationListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return evaluationListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        // evaluationListService dosent has closeList method
        return null
    }

    Map importEvaluationData(GrailsParameterMap params, def file) {
        //get the employee instance
        Employee employee
        EvaluationTemplate evaluationTemplate
        AocCorrespondenceList aocCorrespondenceList
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        try {
            if (file) {
                if (!file?.empty) {
                    def sheetHeader = []
                    def values = []
                    def workbook = WorkbookFactory.create(file?.getInputStream())
                    def sheet = workbook.getSheetAt(0)

                    for (cell in sheet.getRow(0).cellIterator()) {
                        sheetHeader << cell.stringCellValue
                    }

                    def headerFlag = true
                    for (row in sheet.rowIterator()) {
                        if (headerFlag) {
                            headerFlag = false
                            continue
                        }
                        def value = ''
                        def map = [:]
                        for (cell in row.cellIterator()) {
                            switch (cell.cellType) {
                                case 1:
                                    value = cell.stringCellValue
                                    map["${sheetHeader[cell.columnIndex]}"] = value
                                    break
                                case 0:
                                    value = cell.numericCellValue
                                    map["${sheetHeader[cell.columnIndex]}"] = value
                                    break
                                default:
                                    value = ''
                            }
                        }
                        values.add(map)
                    }

                    values.eachWithIndex { def list, Integer i ->
                        if (list) {
                            // prepare aocListRecord params
                            String financialNumber = list["الرقم المالي"]
                            String universalCode = list["نموذج التقييم"]
                            employee = Employee.findByFinancialNumber(financialNumber)
                            if(!employee){
                                throw new Exception("Employee with financial number $financialNumber does not exist")
                            }
                            evaluationTemplate = EvaluationTemplate.findByUniversalCode(universalCode)
                            if(!evaluationTemplate){
                                throw new Exception("Evaluation template with universal code $universalCode does not exist")
                            }

                            aocCorrespondenceList = AocCorrespondenceList.findById(params.long("aocCorrespondenceList.id"))
                            if(employee?.firm?.id != aocCorrespondenceList?.getHrFirmId()){
                                errors << [
                                        field  : "global",
                                        message: messageSource.getMessage("employeeEvaluation.dataExcel.fromAnotherFirm.error.label", null, "importExcel error", LocaleContextHolder.getLocale())
                                ]
                                throw new Exception("Employee not in firm of correspondence")
                            }

                            GrailsParameterMap aocListRecordParams =
                                    new GrailsParameterMap(["aocCorrespondenceList.id":params["aocCorrespondenceList.id"],
                                                            "correspondenceType":params["correspondenceType"],
                                                            "employee.id":employee?.id,
                                                            "evaluationTemplate.id":evaluationTemplate?.id,
                                                            "evaluationTemplate":evaluationTemplate,
                                                            "employee":employee,
                                                            "requestStatus":EnumRequestStatus.ADD_TO_LIST,
                                                            "firm.id":aocCorrespondenceList?.getHrFirmId()],
                                            WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())

                            // get items and marks
                            EvaluationItem evaluationItem
                            def itemId = []
                            String keyOfMark

                            list?.each{ k, v ->
                                println "${k}:${v}"
                                if(k[0] == 'Q'){
                                    evaluationItem = EvaluationItem.createCriteria().get{
                                        eq('universalCode', "${v}")
                                        evaluationSection{
                                            eq('evaluationTemplate.id', evaluationTemplate.id)
                                        }
                                    }
                                    if(!evaluationItem){
                                        throw new Exception("Evaluation Item with universal code $v does not exist")
                                    }
                                    itemId.add(evaluationItem.id)
                                    keyOfMark = "${k}".replace('Q','M')
                                    aocListRecordParams["mark-${evaluationItem.id}"] = list["${keyOfMark}"]
                                }
                            }
                            aocListRecordParams["itemId"] = itemId

                            // save aocListRecord
                            aocListRecordService.save(aocListRecordParams)
                        }
                    }
                }
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false
            }
        } catch (Exception ex) {
            //log.error("Failed to import data ", ex)
            transactionStatus.setRollbackOnly()
            if (!errors) {
                errors << messageSource.getMessage('default.internal.server.error', [ex?.message] as Object[], 'general system error', LocaleContextHolder.getLocale())
            }
        }

        dataMap.put("errors", errors)
        dataMap.put("saved", saved)
        return dataMap
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
